package com.cago.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogPackBinding
import com.cago.dialogs.BaseDialog

class PackDialog(
    private val listener: (String)->Unit, 
    private val name: String? = null
): BaseDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogPackBinding.inflate(inflater)
        binding.apply{
            packName.requestFocus()
            if (name != null) packName.setText(name)
            positive.setOnClickListener {
                listener(packName.text.toString())
                dismiss()
            }
            negative.setOnClickListener { dismiss() }
        }
        return binding.root
    }
}