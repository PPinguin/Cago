package com.cago.home.dialogs.selections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogEditPackBinding
import com.cago.core.dialogs.DialogCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditPackDialog(private val listener: (String)->Unit): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogEditPackBinding.inflate(inflater)
        binding.listener = object : DialogCallback{
            override fun activate(data: Any) {
                listener(data as String)
                dismiss()
            }
        }
        return binding.root
    }
}