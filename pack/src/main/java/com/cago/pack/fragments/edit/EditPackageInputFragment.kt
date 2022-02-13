package com.cago.pack.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cago.core.R
import com.cago.pack.activities.PackActivity
import com.cago.pack.adapters.lists.EditPackListAdapter
import com.cago.core.databinding.FragmentPackageListBinding
import com.cago.pack.dialogs.FieldDialog
import com.cago.pack.dialogs.alerts.InputDialog
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.pack.viewmodels.PackViewModel

class EditPackageInputFragment : Fragment(), EditPackageFragment {

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
            { i -> viewModel.activeInputIndex = i }, 
            viewModel.activeInputIndex
        )
        viewModel.inputsLiveData.observe(viewLifecycleOwner) {
            editPackListAdapter.submitList(it.toMutableList())
            binding?.label?.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        binding?.let {
            it.list.adapter = editPackListAdapter
            it.label.text = getString(R.string.empty_input_edit)
        }
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun valueField() {
        viewModel.getActiveInput()?.let { 
            FieldDialog.buildDialog(it)?.apply { 
                setOnAccepted { 
                    viewModel.defaultInput(it)
                }
            }
                ?.show(requireActivity().supportFragmentManager, getString(R.string.input))
        }
    }

    override fun editField() {
        viewModel.getActiveInput()?.let { 
            InputDialog(
                { n, t -> viewModel.editInput(it, n, t) },
                it, getString(R.string.input_edit)
            )
                .apply { 
                    setOnAccepted { 
                        editPackListAdapter.notifyItemChanged(viewModel.activeInputIndex)
                    }
                }
                .show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.input_edit)
                )
        }
    }

    override fun copyField() {
        viewModel.getActiveInput()?.let { 
            InputDialog(viewModel::addInput, it, getString(R.string.input_copy))
                .show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.input_copy)
                )
        }
    }

    override fun deleteField() {
        viewModel.getActiveInput()?.let {
            QuestionDialog({
                viewModel.deleteInput(it)
                editPackListAdapter.selectItem(--viewModel.activeInputIndex)
            }, getString(R.string.question_delete, it.name))
                .show(requireActivity().supportFragmentManager, getString(R.string.delete))
        }
    }
}
