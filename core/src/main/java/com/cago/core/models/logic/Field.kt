package com.cago.core.models.logic

import java.text.NumberFormat
import java.util.*

abstract class Field(name: String) {
    
    var name = name
        set(value) {
            if(value.contains(Regex("[\\W]"))) throw Exception()
            field = value
        }

    open var value: Double? = null
    
    abstract fun displayValue(): String
    abstract fun displayParams(): String
    
    open fun equal(field: Field): Boolean = field.name == name
    override fun toString(): String = "{$name: $value}"
    
    companion object{
        val format: NumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH).apply {
            maximumFractionDigits = 3
        }
    }
    
}
