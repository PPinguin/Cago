package com.cago.pack.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogOutputBinding
import com.cago.core.dialogs.BaseDialog
import com.cago.core.models.logic.Output
import com.cago.core.R

class OutputDialog(
    private val listener: (String, Boolean, String?) -> Boolean,
    private val output: Output? = null,
    private val label: String? = null,
) : BaseDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DialogOutputBinding.inflate(inflater)
        binding.apply {
            name.requestFocus()
            if (label != null) title.text = label
            if (output != null) {
                name.setText(output.name)
                visible.isChecked = output.visible
            }
            positive.setOnClickListener {
                if (!name.text.isNullOrBlank() && listener(name.text.toString(),
                        visible.isChecked,
                        output?.formula)
                )
                    closeDialog()
                else {
                    name.apply { 
                        error = context.getString(R.string.invalid_name)
                        requestFocus()
                    }
                }
            }
            negative.setOnClickListener { closeDialog() }
        }
        return binding.root
    }
}