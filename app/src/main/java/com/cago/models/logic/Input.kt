package com.cago.models.logic

import com.cago.utils.InputType

abstract class Input(name:String): Field(name) {
    abstract val type: InputType
    override var value: Double? = 0.0
    open fun setValue(value: String){
        this.value = value.toDoubleOrNull()
    }
    override fun displayParams(): String = type.toString()
    override fun equal(field: Field): Boolean {
        field as Input
        return field.type == type && super.equal(field)
    }
}