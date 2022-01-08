package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input

class NumberInput(name: String) : Input(name) {
    override val type = com.cago.core.utils.InputType.Number
    override fun displayValue(): String = format.format(value)
}