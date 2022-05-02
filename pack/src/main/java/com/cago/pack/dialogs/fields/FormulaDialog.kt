package com.cago.pack.dialogs.fields

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.cago.core.databinding.DialogFormulaBinding
import com.cago.core.dialogs.DialogCallback
import com.cago.core.models.logic.Field
import com.cago.core.models.logic.Output
import com.cago.pack.R
import com.cago.pack.adapters.FieldsChoiceAdapter
import com.cago.pack.dialogs.FieldDialog


class FormulaDialog(
    private val output: Output,
    private val inputsList: List<Field>,
    private val outputsList: List<Field>,
) : FieldDialog() {
    var binding: DialogFormulaBinding? = null

    lateinit var inputsAdapter: FieldsChoiceAdapter
    lateinit var outputsAdapter: FieldsChoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputsAdapter = FieldsChoiceAdapter(inputsList) { name ->
            binding?.value?.append("[$name]")
            switch()
        }
        outputsAdapter =
            FieldsChoiceAdapter(outputsList.minus(output)) { name ->
                binding?.value?.append("<$name>")
                switch()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DialogFormulaBinding.inflate(inflater)
        binding?.let {
            it.append = object : DialogCallback {
                override fun activate(data: Any?) {
                    it.value.append(data.toString())
                }
            }
            it.remove = object : DialogCallback {
                override fun activate(data: Any?) {
                    val text = it.value.text.toString()
                    if (text.isEmpty()) return
                    it.value.setText(
                        when {
                            text.last() == ']' -> text.dropLastWhile { c -> c != '[' }.dropLast(1)
                            text.last() == '>' -> text.dropLastWhile { c -> c != '<' }.dropLast(1)
                            else -> text.dropLast(1)
                        }
                    )
                    it.value.setSelection(it.value.text.length)
                }
            }
            it.choose = object : DialogCallback {
                override fun activate(data: Any?) {
                    data as Char
                    if (data == 'i') {
                        it.title.text = getString(R.string.input)
                        it.list.adapter = inputsAdapter
                        it.message.apply { 
                            text = getString(R.string.no_input)
                            isVisible = inputsList.isEmpty()
                        }
                    } else if (data == 'o') {
                        it.title.text = getString(R.string.output)
                        it.list.adapter = outputsAdapter
                        it.message.apply { 
                            text = getString(R.string.no_output)
                            isVisible = outputsList.size == 1
                        }
                    }
                    switch()
                }
            }
            it.back.setOnClickListener { switch() }
            it.name.text = output.name
            it.value.apply{
                showSoftInputOnFocus = false
                setText(output.formula)
                requestFocus()
                inputType = InputType.TYPE_NULL
            }
            it.ok.setOnClickListener {
                if (output.formula != binding?.value?.text.toString()) {
                    output.value = null
                    output.formula = binding?.value?.text.toString()
                }
                dismiss()
            }
        }
        return binding?.root
    }

    fun switch() {
        binding?.let {
            it.keyboard.isVisible = it.choice.isVisible
            it.choice.isVisible = !it.keyboard.isVisible
        }
    }
}