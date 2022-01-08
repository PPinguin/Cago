package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input

class CountInput(name: String) : Input(name) {
    override val type: com.cago.core.utils.InputType = com.cago.core.utils.InputType.Count
    override fun displayValue(): String = value?.toInt().toString()
}