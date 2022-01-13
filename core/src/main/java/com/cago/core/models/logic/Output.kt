package com.cago.core.models.logic

import android.util.Log

class Output(name: String, var visible: Boolean, var formula: String) : Field(name) {
    var error: Boolean = false
    var actions = mutableListOf<String>()
    
    override var value: Double? = null
        set(v){
            Log.d("debugging", "$v")
            field = v
        }
    
    override fun displayValue(): String =
        if (error || value == null) "Error" else format.format(value)

    override fun displayParams(): String = formula
    override fun equal(field: Field): Boolean {
        field as Output
        return field.visible == visible
                && field.formula == formula
                && super.equal(field)
    }
}