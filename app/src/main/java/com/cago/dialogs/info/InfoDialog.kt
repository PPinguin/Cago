package com.cago.dialogs.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.databinding.DialogInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoDialog(val text: String): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogInfoBinding.inflate(inflater)
        binding.text = text
        return binding.root
    }
}