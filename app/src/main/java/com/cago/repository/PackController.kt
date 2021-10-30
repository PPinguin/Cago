package com.cago.repository

import android.content.Context
import android.os.Bundle
import com.cago.models.logic.Field
import com.cago.models.logic.Input
import com.cago.models.logic.Output
import com.cago.repository.callbacks.Callback
import com.cago.repository.managers.FileManager
import com.cago.repository.managers.FirebaseManager
import com.cago.utils.ErrorType
import com.cago.utils.InputType
import java.io.File
import java.util.*
import kotlin.math.pow

class PackController(
    val context: Context,
    private val firebaseManager: FirebaseManager,
    private val fileManager: FileManager
) {

    private var pack: File? = null
    var own: Boolean? = null

    val inputsList = arrayListOf<Field>()
    val outputsList = arrayListOf<Field>()
    var description: String? = null

    private val relationIO = mutableListOf<Pair<Int, Int>>()
    private val relationOO = mutableListOf<Pair<Int, Int>>()

    fun openPack(data: Bundle, callback: Callback<File>) {
        val name = data.getString("name")!!
        if(data.containsKey("path")){
            val path = 
                data.getString("path", "-") + "/" +
                        data.getString("name", "-")
            firebaseManager.downloadPack(path, callback)
        } else {
            if (fileManager.valid(name))
                callback.success(fileManager.getFile(name))
            else 
                callback.failure(ErrorType.ERROR_OPEN)
        }
    }
    
    fun setPack(file: File?){
        if(pack == null && file != null) pack = file
    }

    fun savePack(): Boolean {
        if(own != true) return false
        pack?.writeText("")
        return try {
            pack?.appendText("${Repository.UID}\n")
            inputsList.forEach { input ->
                input as Input
                pack?.appendText("> ${input.name} ${input.type} ${input.displayValue()}\n")
            }
            outputsList.forEach { output ->
                output as Output
                pack?.appendText(
                    "${if (output.visible) '+' else '-'} ${output.name} ${output.formula}\n"
                )
            }
            description?.let {
                pack?.appendText("###\n")
                pack?.appendText(it)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadPack(): Boolean {
        return try {
            var end = false
            description = ""
            pack?.forEachLine { line ->
                if (own == null) own = line == Repository.UID
                else if (!end){
                    val params = line.split(" ")
                    when (params[0]) {
                        ">" -> {
                            createInput(params[1], InputType.valueOf(params[2]))
                            if(params.size > 3 && params[3].isNotEmpty())
                                (inputsList.last() as Input).setValue(params[3])
                        }
                        "-" -> createOutput(params[1], false, params[2])
                        "+" -> createOutput(params[1], true, params[2])
                        "###" -> end = true 
                    }
                } else description += line
            }
            updateAll()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun createInput(name: String, type: InputType) {
        inputsList.add(InputType.buildField(type, name))
    }

    fun deleteInput(input: Input) {
        inputsList.remove(input)
    }

    fun editInput(input: Input, name: String, type: InputType) {
        input.apply {
            if (this.type == type) {
                this.name = name
            } else {
                val pos = inputsList.indexOf(input)
                inputsList.remove(this)
                inputsList.add(pos, InputType.buildField(type, name))
            }
        }
    }

    fun notContainsInput(name: String, position: Int? = null): Boolean =
        inputsList.find { it.name == name }.let {
            it == null || if (position != null) it == inputsList[position] else false
        }

    fun createOutput(name: String, visible: Boolean, formula: String) {
        outputsList.add(Output(name, visible, formula))
        if (formula.isNotEmpty()) parseFormula(outputsList.last() as Output)
    }

    fun deleteOutput(output: Output) {
        outputsList.remove(output)
    }

    fun editOutput(output: Output, name: String, visible: Boolean) {
        output.apply {
            this.name = name
            this.visible = visible
        }
    }

    fun notContainsOutput(name: String, position: Int? = null): Boolean =
        outputsList.find { it.name == name }.let {
            it == null || if (position != null) it == outputsList[position] else false
        }

    fun parseFormula(output: Output) {
        var token = ""
        val stack = Stack<Char>()
        val pos = outputsList.indexOf(output)
        relationIO.removeAll { pair -> pair.second == pos }
        relationOO.removeAll { pair -> pair.second == pos }
        output.apply {
            actions.clear()
            value = null
            for (c in "$formula;") {
                if (c == ' ') continue
                if (c.isDigit() || c in arrayOf('.', '#', '@')) token += c
                else {
                    if (token.isNotEmpty()) {
                        when (token[0]) {
                            '#' -> {
                                token.drop(1).toIntOrNull()?.minus(1)?.let {
                                    (it to pos).let { p ->
                                        if (!relationIO.contains(p)) relationIO.add(p)
                                    }
                                }
                            }
                            '@' -> {
                                token.drop(1).toIntOrNull()?.minus(1)?.let {
                                    (it to pos).let { p ->
                                        if (!relationOO.contains(p)) relationOO.add(p)
                                    }
                                }
                            }
                        }
                        actions.add(token)
                        token = ""
                    }
                    when {
                        (c in arrayOf('+', '-')) -> {
                            while (!stack.empty() && stack.peek() != '|') {
                                actions.add(stack.pop().toString())
                            }
                            stack.push(c)
                        }
                        (c in arrayOf('*', '/')) -> {
                            while (!stack.empty() && stack.peek() in arrayOf('*', '/', '^')) {
                                actions.add(stack.pop().toString())
                            }
                            stack.push(c)
                        }
                        (c == '^') -> {
                            while (!stack.empty() && stack.peek() == '^') {
                                actions.add(stack.pop().toString())
                            }
                            stack.push(c)
                        }
                        (c == '(') -> stack.push('|')
                        (c == ')') -> {
                            while (!stack.empty() && stack.peek() != '|') {
                                actions.add(stack.pop().toString())
                            }
                            if (!stack.empty()) stack.pop()
                        }
                    }
                }
            }
            if (token.isNotEmpty()) actions.add(token)
            while (!stack.empty()) actions.add(stack.pop().toString())
        }
    }

    private fun calculate(output: Output) {
        val s = Stack<Double>()
        val pos = outputsList.indexOf(output)
        output.apply {
            error = actions.isEmpty()
            actions.forEach {
                try {
                    when {
                        it[0] == '#' -> s.push(inputsList[it.drop(1).toInt() - 1].value)
                        it[0] == '@' -> (it.drop(1).toInt() - 1).also { i ->
                            if (i != pos)
                                s.push(outputsList[i].value)
                            else throw Exception()
                        }
                        it == "+" -> s.push(s.pop() + s.pop())
                        it == "-" -> s.push(-s.pop() + s.pop())
                        it == "*" -> s.push(s.pop() * s.pop())
                        it == "/" -> s.push(1 / s.pop() * s.pop())
                        it == "^" -> {
                            s.push(s[s.size - 2].pow(s.pop()))
                            s.removeAt(s.size - 2) // clear stack
                        }
                        else -> s.push(it.toDouble())
                    }
                } catch (e: Exception) {
                    error = true
                    return
                }
            }
            if (!error) value = s.pop()
        }
    }

    fun updateAll() {
        outputsList.forEachIndexed { i, f ->
            if (f.value == null) {
                calculate(f as Output)
                relationOO.filter { it.first == i }
                    .forEach { calculate(outputsList[it.second] as Output) }
            }
        }
    }

    fun update(position: Int) {
        relationIO.filter { it.first == position }.forEach { io ->
            calculate(outputsList[io.second] as Output)
            relationOO.filter { it.first == io.second }
                .forEach { calculate(outputsList[it.second] as Output) }
        }
    }

    fun connectedOutputs(position: Int): List<Int> {
        val set = mutableSetOf<Int>()
        relationIO.filter { it.first == position }.forEach { p ->
            set.add(p.second)
            set.addAll(relationOO.filter { it.first == p.second }.map { it.second })
        }
        return set.toList()
    }

    fun close() {
        if(own != true) pack?.delete()
        
        own = null
        pack = null
        description = null

        inputsList.clear()
        outputsList.clear()

        relationIO.clear()
        relationOO.clear()
    }
}