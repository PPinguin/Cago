package com.cago.models.logic

class Output(name:String, var visible:Boolean, var formula: String): Field(name) {
    var error: Boolean = false
    var actions = mutableListOf<String>()
    override fun displayValue(): String = if (error) "Error" else format.format(value)
    override fun displayParams(): String = formula
    override fun equal(field: Field): Boolean {
        field as Output
        return field.visible == visible
                && field.formula == formula
                && super.equal(field)
    }
}