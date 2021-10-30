package com.cago.models.logic.fields

import com.cago.models.logic.Input
import com.cago.utils.InputType

class PercentInput(name: String) : Input(name) {
    override val type = InputType.Percent
    override fun setValue(value: String) {
        this.value = value.removeSuffix("%").toDoubleOrNull()?.div(100)
    }
    override fun displayValue(): String = "${value?.times(100)?.toInt()}%"
}