package com.cago.pack.dialogs.fields

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.cago.core.databinding.DialogInputPercentBinding
import com.cago.pack.dialogs.FieldDialog
import com.cago.core.models.logic.fields.PercentInput

class PercentInputDialog(val field: PercentInput): FieldDialog() {
    var binding: DialogInputPercentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInputPercentBinding.inflate(inflater)
        binding?.let {
            it.name.text = field.name
            it.value.apply {
                progress = field.value?.times(100)?.toInt() ?: 0
                var s = "$progress%"
                it.text.text = s
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        s = "$progress%"
                        it.text.text = s
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

                })
            }
            it.ok.setOnClickListener { _ ->
                field.value = it.value.progress.toDouble().div(100)
                dismiss()
            }
        }
        return binding?.root
    }
}