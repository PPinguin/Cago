package com.cago.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogInputCountBinding
import com.cago.dialogs.FieldDialog
import com.cago.models.logic.fields.CountInput

class CountInputDialog(val field: CountInput): FieldDialog() {
    private var binding: DialogInputCountBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputCountBinding.inflate(inflater)
        var count = field.value?.toInt() ?: 0
        binding?.let{
            it.name.text = field.name
            it.value.text = "$count"
            it.btnLess.setOnClickListener { _ ->
                count--
                it.value.text = "$count"
            }
            it.btnMore.setOnClickListener { _ ->
                count++
                it.value.text = "$count"
            }
            it.ok.setOnClickListener {
                field.value = count.toDouble()
                dismiss()
            }
        }
        return binding?.root
    }
}