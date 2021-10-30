package com.cago.models.logic.fields

import com.cago.models.logic.Input
import com.cago.utils.InputType

class TextInput(name: String): Input(name) {
    override val type = InputType.Text
    override var value: Double? = null
    var text: String = ""
    override fun setValue(value: String) { text = value }
    override fun displayValue(): String = text
}