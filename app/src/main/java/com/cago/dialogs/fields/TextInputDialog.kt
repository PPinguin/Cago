package com.cago.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogInputTextBinding
import com.cago.dialogs.FieldDialog
import com.cago.models.logic.fields.TextInput

class TextInputDialog(val field: TextInput): FieldDialog() {
    var binding: DialogInputTextBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputTextBinding.inflate(inflater)
        binding?.let {
            it.text.setText(field.displayValue())
            it.ok.setOnClickListener {
                field.text = binding?.text?.text.toString()
                dismiss()
            }
        }
        return binding?.root
    }
}