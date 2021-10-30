package com.cago.dialogs.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogQuestionBinding
import com.cago.dialogs.AlertDialog

class QuestionDialog(
    private val listener: ()->Unit,
    private val question: String
): AlertDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogQuestionBinding.inflate(inflater)
        binding.apply {
            title.text = question
            positive.setOnClickListener { 
                listener()
                dismiss()
            }
            negative.setOnClickListener { dismiss() }
        }
        return binding.root
    }
}