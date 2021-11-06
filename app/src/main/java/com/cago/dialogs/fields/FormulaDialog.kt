package com.cago.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogFormulaBinding
import com.cago.dialogs.FieldDialog
import com.cago.models.logic.Output

class FormulaDialog(private val output: Output): FieldDialog() {
    var binding: DialogFormulaBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFormulaBinding.inflate(inflater)
        binding?.let {
            it.name.text = output.name
            it.value.setText(output.formula)
            it.value.requestFocus()
            it.ok.setOnClickListener {
                if(output.formula != binding?.value?.text.toString()){
                    output.value = null
                    output.formula = binding?.value?.text.toString()
                }
                dismiss()
            }
        }
        return binding?.root
    }
}