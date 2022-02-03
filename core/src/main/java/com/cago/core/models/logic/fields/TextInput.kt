package com.cago.core.models.logic.fields

import com.cago.core.models.logic.Input
import com.cago.core.utils.InputType

class TextInput(name: String): Input(name) {
    override val type = InputType.Text
    override var value: Double? = null
    var text: String = ""
    override fun setValue(value: String) { text = value }
    override fun displayValue(): String = text
}