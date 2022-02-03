package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input
import com.cago.core.utils.InputType

class NumberInput(name: String) : Input(name) {
    override val type = InputType.Number
    override fun displayValue(): String = format.format(value)
}