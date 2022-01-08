package com.cago.pack.fragments.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cago.core.R
import com.cago.pack.PackActivity
import com.cago.pack.adapters.lists.EditPackListAdapter
import com.cago.core.databinding.FragmentPackageListBinding
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.pack.dialogs.alerts.OutputDialog
import com.cago.pack.dialogs.fields.FormulaDialog
import com.cago.pack.viewmodels.PackViewModel

class EditPackageOutputFragment : Fragment(), EditPackageFragment {

    private var binding: FragmentPackageListBinding? = null
    private lateinit var viewModel: PackViewModel
    private lateinit var editPackListAdapter: EditPackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as PackActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPackageListBinding.inflate(inflater)
        editPackListAdapter = EditPackListAdapter(
            { i -> viewModel.activeOutputIndex = i },
            viewModel.activeOutputIndex
        )
        viewModel.outputsLiveData.observe(viewLifecycleOwner) {
            editPackListAdapter.submitList(it.toMutableList())
            Log.d("debugging_fr", "$it")
            binding?.label?.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        binding?.let {
            it.list.adapter = editPackListAdapter
            it.label.text = getString(R.string.empty_output_edit)
        }
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun valueField() {
        viewModel.getActiveOutput()?.let {
            FormulaDialog(it)
                .apply {
                    setOnAccepted {
                        editPackListAdapter.notifyItemChanged(viewModel.getOutputIndex(it))
                        if(it.value == null) viewModel.handleOutput(it)
                    }
                }
                .show(requireActivity().supportFragmentManager, getString(R.string.formula))
        }
    }

    override fun editField() {
        viewModel.getActiveOutput()?.let {
            OutputDialog(
                { n, v, _ -> viewModel.editOutput(it, n, v) },
                it, getString(R.string.output_edit)
            )
                .apply { 
                    setOnAccepted { 
                        editPackListAdapter.notifyItemChanged(viewModel.activeOutputIndex)
                    }
                }
                .show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.output_edit)
                )
        }
    }

    override fun copyField() {
        viewModel.getActiveOutput()?.let {
            OutputDialog(viewModel::addOutput, it, getString(R.string.output_copy))
                .show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.output_copy)
                )
        }
    }

    override fun deleteField() {
        viewModel.getActiveOutput()?.let {
            QuestionDialog({
                viewModel.deleteOutput(it)
                editPackListAdapter.selectItem(--viewModel.activeOutputIndex)
            }, getString(R.string.question_delete, it.name))
                .show(requireActivity().supportFragmentManager, getString(R.string.delete))
        }
    }
}
