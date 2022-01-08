package com.cago.pack.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cago.core.R
import com.cago.core.databinding.DialogInputBinding
import com.cago.core.dialogs.BaseDialog
import com.cago.core.models.logic.Input
import com.cago.core.utils.InputType

class InputDialog(
    private val listener: (String, InputType) -> Boolean,
    private val input: Input? = null,
    private val label: String? = null,
) : BaseDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DialogInputBinding.inflate(inflater)
        binding.apply {
            name.requestFocus()
            if (label != null) title.text = label
            spinner.adapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                InputType.values()
            )
            if (input != null) {
                name.setText(input.name)
                spinner.setSelection(InputType.values().indexOf(input.type))
            }
            positive.setOnClickListener {
                if (name.text.isNotEmpty() && listener(name.text.toString(),
                        spinner.selectedItem as InputType)
                )
                    dismiss()
                else name.requestFocus()
            }
            negative.setOnClickListener { dismiss() }
        }
        return binding.root
    }
}