package com.cago.core.models.logic

import com.cago.core.models.logic.parser.Token

class Output(name: String, var visible: Boolean, var formula: String) : Field(name) {
    var error: Boolean = false
    var actions = mutableListOf<Token>()
    
    override var value: Double? = null
    
    override fun displayValue(): String =
        if (error || value == null) "Error" else format.format(value)

    override fun displayParams(): String = if(visible) "visible" else "invisible"
    override fun equal(field: Field): Boolean {
        field as Output
        return field.visible == visible
                && field.formula == formula
                && super.equal(field)
    }
}