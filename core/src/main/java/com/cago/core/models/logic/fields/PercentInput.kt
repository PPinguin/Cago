package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input
import com.cago.core.utils.InputType

class PercentInput(name: String) : Input(name) {
    override val type = InputType.Percent
    override fun setValue(value: String) {
        this.value = value.removeSuffix("%").toDoubleOrNull()?.div(100)
    }
    override fun displayValue(): String = "${value?.times(100)?.toInt()}%"
}