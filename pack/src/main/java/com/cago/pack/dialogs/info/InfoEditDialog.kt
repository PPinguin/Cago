package com.cago.pack.dialogs.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cago.core.databinding.DialogInfoEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoEditDialog(val text: String, val listener: (String)->Unit): BottomSheetDialogFragment() {

    var binding: DialogInfoEditBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInfoEditBinding.inflate(inflater)
        binding?.input?.setText(text)
        return binding?.root
    }

    override fun onDestroyView() {
        binding?.let{
            listener(it.input.text.toString())
            binding = null
        }
        super.onDestroyView()
    }
}