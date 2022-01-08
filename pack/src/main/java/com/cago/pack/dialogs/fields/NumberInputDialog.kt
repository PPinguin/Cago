package com.cago.pack.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogInputNumberBinding
import com.cago.pack.dialogs.FieldDialog
import com.cago.core.models.logic.fields.NumberInput

class NumberInputDialog(val field: NumberInput): FieldDialog() {
    var binding: DialogInputNumberBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputNumberBinding.inflate(inflater)
        binding?.let {
            it.name.text = field.name 
            it.value.setText(field.displayValue())
            it.value.requestFocus()
            it.ok.setOnClickListener {
                binding?.value?.let{ et ->
                    if (et.text.isEmpty()) et.setText("0")
                    field.value = et.text.toString().toDoubleOrNull()
                }
                dismiss()
            }
        }
        return binding?.root
    }
}