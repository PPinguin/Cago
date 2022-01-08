package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input

class TextInput(name: String): Input(name) {
    override val type = com.cago.core.utils.InputType.Text
    override var value: Double? = null
    var text: String = ""
    override fun setValue(value: String) { text = value }
    override fun displayValue(): String = text
}