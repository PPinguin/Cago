package com.cago.models.logic.fields

import com.cago.models.logic.Input
import com.cago.utils.InputType

class NumberInput(name: String) : Input(name) {
    override val type = InputType.Number
    override fun displayValue(): String = format.format(value)
}