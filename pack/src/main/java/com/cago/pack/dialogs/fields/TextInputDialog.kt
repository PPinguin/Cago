package com.cago.pack.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogInputTextBinding
import com.cago.pack.dialogs.FieldDialog
import com.cago.core.models.logic.fields.TextInput

class TextInputDialog(val field: TextInput): FieldDialog() {
    var binding: DialogInputTextBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputTextBinding.inflate(inflater)
        binding?.let {
            it.name.text = field.name
            it.text.setText(field.displayValue())
            it.text.requestFocus()
            it.ok.setOnClickListener {
                field.text = binding?.text?.text.toString()
                dismiss()
            }
        }
        return binding?.root
    }
}