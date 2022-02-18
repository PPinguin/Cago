package com.cago.core.repository

import android.os.Bundle
import com.cago.core.models.logic.Field
import com.cago.core.models.logic.Input
import com.cago.core.models.logic.Output
import com.cago.core.repository.callbacks.Callback
import com.cago.core.repository.managers.FileManager
import com.cago.core.repository.managers.FirebaseManager
import com.cago.core.utils.ErrorType
import com.cago.core.utils.GlobalUtils.UID
import com.cago.core.utils.InputType
import java.io.File
import java.util.*
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class PackController(
    private val firebaseManager: FirebaseManager,
    private val fileManager: FileManager,
) {

    private var pack: File? = null
    var own: Boolean? = null

    val inputsList = arrayListOf<Field>()
    val outputsList = arrayListOf<Field>()
    var description: String? = null

    private val relationIO = mutableListOf<Pair<Int, Int>>()
    private val relationOO = mutableListOf<Pair<Int, Int>>()

    private val visibleOutputs = mutableListOf<Int>()

    fun openPack(data: Bundle, callback: Callback<File>) {
        val name = data.getString("name")!!
        if (data.containsKey("path")) {
                firebaseManager.downloadPack(
                    data.getString("name", "-"),
                    data.getString("path", "-"),
                    callback)
        } else {
            if (fileManager.valid(name))
                callback.success(fileManager.getFile(name))
            else
                callback.failure(ErrorType.ERROR_OPEN)
        }
    }

    fun setPack(file: File?) {
        if (pack == null && file != null) pack = file
    }

    fun savePack(): Boolean {
        if (own != true) return false
        pack?.writeText("")
        val content = StringBuilder("$UID\n") 
        return try {
            inputsList.forEach { input ->
                input as Input
                input.setDefaultValue()
                content.append(
                    ">|${input.name}|${input.type}|${input.displayValue()}\n"
                ) 
            }
            outputsList.forEach { output ->
                output as Output
                content.append(
                    "${if (output.visible) '+' else '-'}|${output.name}|${output.formula}\n"
                )
            }
            description?.let {
                content.append("###\n")
                content.append(it)
            }
            pack?.writeText(content.toString())
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
                if (own == null) own = line == UID
                else if (!end) {
                    val params = line.split("|")
                    when (params[0]) {
                        ">" -> {
                            createInput(params[1], InputType.valueOf(params[2]))
                            if (params.size > 3 && params[3].isNotEmpty()) {
                                (inputsList.last() as Input).apply {
                                    setValue(params[3])
                                    updateDefault()
                                }
                            }
                        }
                        "-" -> createOutput(params[1], false, params[2])
                        "+" -> createOutput(params[1], true, params[2])
                        "###" -> end = true
                    }
                } else description += line
            }
            extractVisibleOutputs()
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
        val pos = inputsList.indexOf(input)
        val cache = mutableListOf<Pair<Int, Int>>()
        var a: Int; var b: Int
        relationIO.forEach {
            if (it.first > pos || it.second > pos){
                a = if(it.first > pos) it.first-1 else it.first
                b = if(it.second > pos) it.second-1 else it.second
                cache.add(it.copy(first = a, second = b))
            }
        }
        relationIO.removeAll { pair -> pair.first >= pos }
        relationIO.addAll(cache)
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
        if (formula.isNotEmpty()) handleOutput(outputsList.last() as Output)
    }

    fun deleteOutput(output: Output) {
        outputsList.remove(output)
        val pos = outputsList.indexOf(output)
        relationIO.removeAll { pair -> pair.second == pos }
        val cache = mutableListOf<Pair<Int, Int>>()
        var a: Int; var b: Int
        relationOO.forEach {
            if (it.first > pos || it.second > pos){
                a = if(it.first > pos) it.first-1 else it.first 
                b = if(it.second > pos) it.second-1 else it.second
                cache.add(it.copy(first = a, second = b))
            }
        }
        relationOO.removeAll { pair -> pair.second >= pos || pair.first >= pos }
        relationOO.addAll(cache)
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

    fun handleOutput(output: Output) {
        output.apply {
            if (parseFormula(this)) {
                calculate(this)
                actions.clear()
                actions.add(value.toString())
            }
        }
    }

    private fun parseFormula(output: Output): Boolean {
        var token = ""
        var independent = true
        val stack = Stack<Char>()
        val pos = outputsList.indexOf(output)
        relationIO.removeAll { pair -> pair.second == pos }
        relationOO.removeAll { pair -> pair.second == pos }
        output.apply {
            actions.clear()
            for (c in "$formula;") {
                if (c == ' ') continue
                if (c.isDigit() || c in arrayOf('.', '#', '@')) token += c
                else {
                    if (token.isNotEmpty()) {
                        when (token[0]) {
                            '#' -> {
                                token.drop(1).toIntOrNull()?.minus(1)?.let {
                                    relationIO.add((it to pos))
                                    independent = false
                                }
                            }
                            '@' -> {
                                token.drop(1).toIntOrNull()?.minus(1)?.let {
                                    if (pos == it) return true
                                    val s = Stack<Int>()
                                    s.push(it)
                                    while (!s.empty()) {
                                        val i = s.pop()
                                        relationOO.add((i to pos))
                                        relationOO
                                            .forEach { oo -> if (oo.second == i) s.push(oo.first) }
                                    }
                                    independent = false
                                }
                            }
                        }
                        actions.add(token)
                    }
                    when {
                        (c == '+') -> {
                            while (!stack.empty() && stack.peek() != '|') {
                                actions.add(stack.pop().toString())
                            }
                            stack.push('+')
                        }
                        (c == '-') -> {
                            if(token.isNotEmpty()) {
                                while (!stack.empty() && stack.peek() != '|') {
                                    actions.add(stack.pop().toString())
                                }
                                stack.push('-')
                            } else stack.push('!')
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
                    if(token.isNotEmpty()) token = ""
                }
            }
            if (token.isNotEmpty()) actions.add(token)
            while (!stack.empty()) actions.add(stack.pop().toString())
        }
        return independent
    }

    private fun calculate(output: Output) {
        val stack = Stack<Double>()
        val pos = outputsList.indexOf(output)
        output.apply {
            error = actions.isEmpty()
            actions.forEach {
                try {
                    when {
                        it[0] == '#' -> stack.push(inputsList[it.drop(1).toInt() - 1].value)
                        it[0] == '@' -> (it.drop(1).toInt() - 1).also { i ->
                            if (i != pos)
                                stack.push(outputsList[i].value)
                            else throw Exception()
                        }
                        it == "!" -> stack.push(-stack.pop())
                        it == "+" -> stack.push(stack.pop() + stack.pop())
                        it == "-" -> stack.push(-stack.pop() + stack.pop())
                        it == "*" -> stack.push(stack.pop() * stack.pop())
                        it == "/" -> stack.push(1 / stack.pop() * stack.pop())
                        it == "^" -> {
                            stack.push(stack[stack.size - 2].pow(stack.pop()))
                            stack.removeAt(stack.size - 2) // clear stack
                        }
                        else -> stack.push(it.toDouble())
                    }
                } catch (e: Exception) {
                    error = true
                    value = null
                    return
                }
            }
            if (!error) value = stack.pop()
        }
    }
    
    fun extractVisibleOutputs(){
        visibleOutputs.clear()
        outputsList
            .forEachIndexed {index, output -> if((output as Output).visible) visibleOutputs.add(index) }
    }

    private fun updateAll() {
        val list = (0..outputsList.size).toMutableList()
        relationOO.forEach { list.remove(it.second) }
        list.forEach { updateOO(it) }
    }

    fun updateIO(position: Int) {
        relationIO
            .forEach { io -> if (io.first == position) updateOO(io.second) }
    }

    fun updateOO(position: Int) {
        val queue = mutableListOf(position)
        relationOO
            .forEach { if (it.first == position) queue.add(it.second) }
        queue.forEach { calculate(outputsList[it] as Output) }
    }

    fun connectedOutputs(position: Int): List<Int> {
        val set = mutableSetOf<Int>()
        relationIO.filter { it.first == position }.forEach { p ->
            set.add(p.second)
            set.addAll(relationOO.filter { it.first == p.second }.map { it.second })
        }
        val result = mutableListOf<Int>()
        visibleOutputs.forEachIndexed { index, i -> if (set.contains(i)) result.add(index) }
        return result
    }
    
    fun getOutputConnections(index: Int): List<Int>{
        val result = mutableListOf<Int>()
        relationOO.forEach { if(it.first == index) result.add(it.second) }
        return result
    }


    fun getInputConnections(index: Int): List<Int>{
        val result = mutableListOf<Int>()
        relationIO.forEach { if(it.first == index) result.add(it.second) }
        return result
    }

    fun close() {
        if (own != true) pack?.delete()

        own = null
        pack = null
        description = null

        inputsList.clear()
        outputsList.clear()

        relationIO.clear()
        relationOO.clear()
    }
}