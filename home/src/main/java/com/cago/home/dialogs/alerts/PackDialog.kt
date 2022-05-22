package com.cago.home.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogPackBinding
import com.cago.core.dialogs.BaseDialog

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
            packName.requestFocus()
            positive.setOnClickListener {
                listener(packName.text.toString())
                closeDialog()
            }
            negative.setOnClickListener { closeDialog() }
        }
        return binding.root
    }
}