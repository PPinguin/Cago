package com.cago.pack.dialogs.fields

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogInputPercentBinding
import com.cago.core.models.logic.fields.PercentInput
import com.cago.pack.dialogs.FieldDialog

class PercentInputDialog(val field: PercentInput): FieldDialog() {
    var binding: DialogInputPercentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputPercentBinding.inflate(inflater)
        binding?.let {
            it.name.text = field.name
            it.value.setText(field.displayValue())
            it.value.apply { 
                Selection.setSelection(text, text?.length?.minus(1) ?: 0)
            }
            it.value.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.endsWith("%") != true){
                        it.value.apply {
                            setText("$text%")
                            Selection.setSelection(text, text?.length?.minus(1) ?: 0)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    s.toString().dropLast(1).toDoubleOrNull()?.let { v -> 
                        if(v>100) it.value.setText(100.toString())
                    }
                }
            })
            it.ok.setOnClickListener { _ ->
                field.value = it.value.text.toString().dropLast(1).toDouble().div(100)
                dismiss()
            }
        }
        return binding?.root
    }
}