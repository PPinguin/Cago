package com.cago.fragments.pack.usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cago.R
import com.cago.activities.PackActivity
import com.cago.adapters.lists.PackListAdapter
import com.cago.databinding.FragmentPackageListBinding
import com.cago.models.logic.Output
import com.cago.viewmodels.PackViewModel

class PackageOutputFragment : Fragment() {

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
        packListAdapter = PackListAdapter()
        viewModel.outputsLiveData.observe(viewLifecycleOwner) {
            packListAdapter.submitList(it.filter { f -> (f as Output).visible })
            binding?.label?.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.editedInput.observe(viewLifecycleOwner) {
            viewModel.getConnections(it).forEach { i -> packListAdapter.notifyItemChanged(i) }
        }
        binding?.let {
            it.list.adapter = packListAdapter
            it.label.text = getString(R.string.empty_output_list)
        }
        return binding?.root
    }
}