package com.cago.core.models.logic

import com.cago.core.models.logic.fields.*
import com.cago.core.utils.InputType

abstract class Input(name:String): Field(name) {
    abstract val type: InputType
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

    companion object {
        fun buildField(type: InputType, name: String) =
            when (type) {
                InputType.Number -> NumberInput(name)
                InputType.Percent -> PercentInput(name)
                InputType.Boolean -> BooleanInput(name)
                InputType.Count -> CountInput(name)
                InputType.Text -> TextInput(name)
            }
    }
}