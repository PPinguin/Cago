package com.cago.core.models.logic.parser

interface Token

class NumberToken(val number: Double): Token{
    override fun toString(): String = "$number"
}

class OperatorToken(val type: Operator): Token{
    override fun toString(): String = "$type"
}

class InputToken(val index: Int): Token{
    override fun toString(): String = "$index"
}

class OutputToken(val index: Int): Token{
    override fun toString(): String = "$index"
}

enum class Operator(val priority: Byte){
    SUM(1), 
    SUB(1), 
    MUL(2), 
    DIV(2), 
    INV(4), 
    POW(3), 
    BRL(0),
    BRR(0),
    NONE(-1)
}