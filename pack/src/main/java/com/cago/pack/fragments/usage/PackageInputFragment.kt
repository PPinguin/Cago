package com.cago.pack.fragments.usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cago.core.R
import com.cago.pack.activities.PackActivity
import com.cago.pack.adapters.lists.PackListAdapter
import com.cago.core.databinding.FragmentPackageListBinding
import com.cago.pack.dialogs.FieldDialog
import com.cago.pack.viewmodels.PackViewModel

class PackageInputFragment : Fragment() {

    private var binding: FragmentPackageListBinding? = null
    private lateinit var viewModel: PackViewModel
    private lateinit var packListAdapter: PackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as PackActivity).viewModel
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPackageListBinding.inflate(inflater)
        packListAdapter = PackListAdapter { position ->
            val field = viewModel.getInput(position)
            FieldDialog.buildDialog(field)?.apply {
                setOnAccepted {
                    packListAdapter.notifyItemChanged(position)
                    viewModel.update(position)
                }
            }
                ?.show(requireActivity().supportFragmentManager, getString(R.string.input))
        }
        binding?.let {
            it.list.adapter = packListAdapter
            it.label.text = getString(R.string.empty_input_list)
        }
        viewModel.inputsLiveData.observe(viewLifecycleOwner) {
            packListAdapter.submitList(it)
            binding?.label?.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        }
        return binding?.root
    }
}