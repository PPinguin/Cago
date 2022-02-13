package com.cago.core.models.logic

abstract class Input(name:String): Field(name) {
    abstract val type: com.cago.core.utils.InputType
    override var value: Double? = 0.0
    var default: Double = 0.0
    open fun setValue(value: String){
        this.value = value.toDoubleOrNull()
    }
    override fun displayParams(): String = type.toString()
    override fun equal(field: Field): Boolean {
        field as Input
        return field.type == type && super.equal(field)
    }
    fun updateDefault(){ default = value?:0.0 }
    fun setDefaultValue(){ value = default }
}