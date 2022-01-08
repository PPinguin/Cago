package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input

class BooleanInput(name: String) : Input(name) {
    override val type = com.cago.core.utils.InputType.Boolean
    override fun setValue(value: String) {
        this.value = if (value.toBoolean()) 1.0 else 0.0
    }
    override fun displayValue(): String = if (value == 1.0) "true" else "false"
}