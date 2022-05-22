package com.cago.core.repository

import android.net.Uri
import android.os.Bundle
import com.cago.core.models.logic.Field
import com.cago.core.models.logic.Input
import com.cago.core.models.logic.Output
import com.cago.core.models.logic.parser.*
import com.cago.core.repository.callbacks.Callback
import com.cago.core.repository.managers.FileManager
import com.cago.core.utils.ErrorType
import com.cago.core.utils.InputType
import java.io.File
import java.util.*
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class PackController(
    private val fileManager: FileManager,
) {

    private var pack: File? = null
    private var own: Boolean? = null 

    val inputsList = arrayListOf<Field>()
    val outputsList = arrayListOf<Field>()
    
    var description: String? = null
    
    private val parser = Parser()

    private val relationIO = mutableListOf<Pair<Int, Int>>()
    private val relationOO = mutableListOf<Pair<Int, Int>>()

    private val visibleOutputs = mutableListOf<Int>()

    fun openPack(data: Bundle, callback: Callback<File>) {
        val name = data.getString("name")!!
        val path = data.getString("path")
        own = path == null
        if (own == true) {
            if (fileManager.valid(name, path)) callback.success(fileManager.getFile(name, path))
            else callback.failure(ErrorType.ERROR_OPEN)
        } else {
            val file = fileManager.cache(Uri.parse(path), name)
            if(file != null) callback.success(file)
            else callback.failure(ErrorType.ERROR_OPEN)
        }
    }

    fun setPack(file: File?) {
        if (pack == null && file != null) pack = file
    }

    fun savePack(): Boolean {
        pack?.writeText("")
        val content = StringBuilder("") 
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
                if (!end) {
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
        inputsList.add(Input.buildField(type, name))
    }

    fun deleteInput(input: Input) {
        inputsList.remove(input)
        val pos = inputsList.indexOf(input)
        for (i in 0 until relationIO.size){
            if (relationIO[i].first > pos)
                relationIO[i] = relationIO[i].copy(first = relationIO[i].first-1)
        }
        relationIO.removeAll { pair -> pair.first == pos }
    }

    fun editInput(input: Input, name: String, type: InputType) {
        input.apply {
            if (this.type == type) {
                if(this.name != name){
                    val index = inputsList.indexOf(input)
                    relationIO.filter { p -> p.first ==  index}.forEach { p -> 
                        (outputsList[p.second] as Output).let {
                            it.formula = it.formula
                                .replace("[${this.name}]", "[$name]")
                        }
                    }
                    this.name = name
                }
            } else {
                val pos = inputsList.indexOf(input)
                inputsList.remove(this)
                inputsList.add(pos, Input.buildField(type, name))
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
        for (i in 0 until relationOO.size){
            if (relationOO[i].first > pos)
                relationOO[i] = relationOO[i].copy(first = relationOO[i].first-1)
        }
        relationOO.removeAll { pair -> 
            pair.second == pos || 
            pair.first == pos || 
            pair.second == outputsList.size 
        }
    }

    fun editOutput(output: Output, name: String, visible: Boolean) {
        output.apply {
            if(this.name != name){
                val index = outputsList.indexOf(output)
                relationOO.filter { p -> p.first ==  index}.forEach { p ->
                    (outputsList[p.second] as Output).let {
                        it.formula = it.formula
                            .replace("<${this.name}>", "<$name>")
                    }
                }
                this.name = name
            }
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
            parseFormula(this)
            if (actions.find { t -> t is InputToken || t is OutputToken } == null) {
                calculate(this)
                actions.clear()
                actions.add(NumberToken(value?:0.0))
            }
        }
    }

    private fun parseFormula(output: Output) {
        val pos = outputsList.indexOf(output)

        relationIO.removeAll { pair -> pair.second == pos }
        relationOO.removeAll { pair -> pair.second == pos }
        
        output.actions = parser.parse(output.formula, inputsList, outputsList).onEach { t ->
            when(t){
                is InputToken -> {
                    relationIO.add((t.index to pos))
                }
                is OutputToken -> {
                    val s = Stack<Int>()
                    s.push(t.index)
                    while (!s.empty()) {
                        val i = s.pop()
                        relationOO.add((i to pos))
                        relationOO
                            .forEach { oo -> if (oo.second == i) s.push(oo.first) }
                    }
                }
            }
        }.toMutableList()
    }

    private fun calculate(output: Output) {
        val stack = Stack<Double>()
        val pos = outputsList.indexOf(output)
        output.apply {
            error = actions.isEmpty()
            actions.forEach {
                try {
                    when(it) {
                        is InputToken -> stack.push(inputsList[it.index].value)
                        is OutputToken -> it.index.also { i ->
                            if (i != pos)
                                stack.push(outputsList[i].value)
                            else throw Exception()
                        }
                        is OperatorToken -> {
                            when(it.type) {
                                Operator.INV -> stack.push(-stack.pop())
                                Operator.SUM -> stack.push(stack.pop() + stack.pop())
                                Operator.SUB -> stack.push(-stack.pop() + stack.pop())
                                Operator.MUL -> stack.push(stack.pop() * stack.pop())
                                Operator.DIV -> stack.push(1 / stack.pop() * stack.pop())
                                Operator.POW -> {
                                    stack.push(stack[stack.size - 2].pow(stack.pop()))
                                    stack.removeAt(stack.size - 2) // clear stack
                                }
                                else -> throw Exception("Unknown operator")
                            }
                        }
                        is NumberToken -> stack.push(it.number) 
                        else -> throw Exception("Unknown token")
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
    
    fun isOwn() = own == true

    fun close() {
        if(own != true){
            fileManager.deletePack(pack!!.name, "cache")
        }
        
        pack = null
        description = null
        own = null

        inputsList.clear()
        outputsList.clear()

        relationIO.clear()
        relationOO.clear()
    }
}