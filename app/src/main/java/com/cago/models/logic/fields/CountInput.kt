package com.cago.models.logic.fields

import com.cago.models.logic.Input
import com.cago.utils.InputType

class CountInput(name: String) : Input(name) {
    override val type: InputType = InputType.Count
    override fun displayValue(): String = value?.toInt().toString()
}