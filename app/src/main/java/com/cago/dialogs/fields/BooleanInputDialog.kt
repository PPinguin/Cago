package com.cago.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogInputBooleanBinding
import com.cago.dialogs.FieldDialog
import com.cago.models.logic.fields.BooleanInput

class BooleanInputDialog(val field: BooleanInput): FieldDialog() {
    var binding: DialogInputBooleanBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputBooleanBinding.inflate(inflater)
        binding?.let {
            it.name.text = field.name
            it.value.isChecked = field.value == 1.0
            it.value.setOnClickListener { 
                field.value = if (binding?.value?.isChecked == true) 1.0 else 0.0
            }
        }
        return binding?.root
    }
}