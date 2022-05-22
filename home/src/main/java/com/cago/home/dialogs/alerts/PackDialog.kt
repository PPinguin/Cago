package com.cago.home.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.R
import com.cago.core.databinding.DialogPackBinding
import com.cago.core.dialogs.BaseDialog
import com.cago.core.utils.InputType

class PackDialog(
    private val listener: (String)->Unit 
): BaseDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogPackBinding.inflate(inflater)
        binding.apply{
            name.requestFocus()
            positive.setOnClickListener {
                if (!name.text.isNullOrBlank()) {
                    listener(name.text.toString())
                    closeDialog()
                } else {
                    name.apply{
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