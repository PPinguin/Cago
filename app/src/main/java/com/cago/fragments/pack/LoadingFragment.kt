package com.cago.fragments.pack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cago.R
import com.cago.activities.PackActivity
import com.cago.databinding.FragmentLoadingBinding
import com.cago.viewmodels.PackViewModel

class LoadingFragment : Fragment() {

    private var binding: FragmentLoadingBinding? = null
    private lateinit var viewModel: PackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as PackActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val data = requireActivity().intent.data?.pathSegments
        val bundle = if (data != null) 
            bundleOf(
                "path" to data[0],
                "name" to data[1]
            ) 
        else 
            requireActivity().intent.extras
        if (bundle == null) findNavController().popBackStack()
        binding = FragmentLoadingBinding.inflate(inflater)
        binding?.let { it.pack = bundle!!.getString("name") }
        viewModel.openPack(bundle!!)
        viewModel.pack.observe(viewLifecycleOwner) {
            if (it != null)
                findNavController().navigate(R.id.action_loadingFragment_to_packFragment)
            else
                requireActivity().finish()
        }
        return binding?.root
    }
}