package com.cago.core.repository

import com.cago.core.models.logic.Field
import com.cago.core.models.logic.parser.*
import java.util.*

class Parser {
    
    private var token: String? = null
    private var formula: String = ""
    
    private var startIndex = 0
    private var charIndex = startIndex
    
    private val stack = Stack<OperatorToken>()
    
    private val actions = mutableListOf<Token>()
    
    fun parse(formula: String, inputList: List<Field>, outputList: List<Field>): List<Token> {
        startIndex = 0
        this.formula = "$formula;"
        actions.clear()
        stack.clear()
        while (this.formula[startIndex] != ';'){
            when {
                this.formula[startIndex].isDigit() -> parseNumber()
                this.formula[startIndex] == '[' -> parseInput(inputList)
                this.formula[startIndex] == '<' -> parseOutput(outputList)
                this.formula[startIndex] == ' ' -> continue
                else -> parseOperation()
            }
        }
        while (!stack.empty()) actions.add(stack.pop())
        token = null
        return actions
    }
    
    private fun parseNumber(){
        charIndex = startIndex+1
        while ((formula[charIndex].isDigit() || formula[charIndex] == '.') 
            && charIndex < formula.length-1) charIndex++
        token = formula.subSequence(startIndex, charIndex).toString()
        actions.add(NumberToken(token!!.toDouble()))
        startIndex = charIndex
    }
    
    private fun parseInput(inputList: List<Field>){
        startIndex++
        charIndex = startIndex
        while (formula[charIndex] != ']') charIndex++
        token = formula.subSequence(startIndex, charIndex).toString()
        actions.add(InputToken(inputList.indexOfFirst { f -> f.name == token }))
        startIndex = charIndex+1
    }

    private fun parseOutput(outputList: List<Field>){
        startIndex++
        charIndex = startIndex
        while (formula[charIndex] != '>') charIndex++
        token = formula.subSequence(startIndex, charIndex).toString()
        actions.add(OutputToken(outputList.indexOfFirst { f -> f.name == token }))
        startIndex = charIndex+1
    }
    
    private fun parseOperation(){
        var operator: Operator =
            when(formula[startIndex]){
                '+' -> Operator.SUM
                '-' -> Operator.SUB
                '*' -> Operator.MUL
                '/' -> Operator.DIV
                '^' -> Operator.POW
                '(' -> Operator.BRL
                ')' -> Operator.BRR
                else -> Operator.NONE
            }
        if(operator == Operator.SUB && token == null) operator = Operator.INV
        
        if(operator == Operator.BRL){
            stack.push(OperatorToken(operator))
        }
        else if(operator == Operator.BRR) {
            while (!stack.empty() && stack.peek().type != Operator.BRL) {
                actions.add(stack.pop())
            }
            if (!stack.empty()) stack.pop()
        }
        else {
            while (!stack.empty() && stack.peek().type.priority >= operator.priority) {
                actions.add(stack.pop())
            }
            stack.push(OperatorToken(operator))
        }
        startIndex++
        token = null
    }
}