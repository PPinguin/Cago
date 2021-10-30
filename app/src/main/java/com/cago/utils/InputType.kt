package com.cago.utils

import com.cago.models.logic.fields.*

enum class InputType {
    Number,
    Percent,
    Count,
    Text,
    Boolean;

    companion object {
        fun buildField(type: InputType, name: String) =
            when (type) {
                Number -> NumberInput(name)
                Percent -> PercentInput(name)
                Boolean -> BooleanInput(name)
                Count -> CountInput(name)
                Text -> TextInput(name)
            }
    }
}