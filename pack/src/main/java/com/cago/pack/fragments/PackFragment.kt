package com.cago.pack.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cago.pack.PackActivity
import com.cago.pack.adapters.PackFragmentsAdapter
import com.cago.core.databinding.FragmentPackBinding
import com.cago.core.R
import com.cago.pack.dialogs.info.InfoDialog
import com.cago.pack.viewmodels.PackViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class PackFragment : Fragment() {

    private var binding: FragmentPackBinding? = null
    private lateinit var adapter: PackFragmentsAdapter
    private lateinit var viewModel: PackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as PackActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPackBinding.inflate(inflater)
        adapter = PackFragmentsAdapter(this)
        // initialize UI
        binding?.let {
            it.pager.adapter = adapter
            it.toolbar.overflowIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_options)
            it.toolbar.apply {
                title = viewModel.pack.value
                inflateMenu(R.menu.pack_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.info -> {
                            openInfo()
                            true
                        }
                        R.id.help -> {
                            (requireActivity() as PackActivity).help()
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }
                it.fabEdit.visibility =
                    if (viewModel.isOwnUser() == true) View.VISIBLE else View.GONE
                it.fabEdit.setOnClickListener { editPack() }
                TabLayoutMediator(it.tabs, it.pager) { tab, position ->
                    tab.text = if (position % 2 == 0)
                        getString(R.string.input)
                    else
                        getString(R.string.output)
                }.attach()
            }
            viewModel.message.observe(viewLifecycleOwner) { msg ->
                if (msg.isNotEmpty()) {
                    Snackbar.make(it.root, msg, Snackbar.LENGTH_SHORT).show()
                    viewModel.message.value = ""
                }
            }
        }
        return binding?.root
    }

    private fun editPack() {
        findNavController()
            .navigate(R.id.action_packFragment_to_editPackFragment)
    }

    private fun openInfo() {
        InfoDialog(viewModel.getDescription())
            .show(
                requireActivity().supportFragmentManager,
                getString(R.string.description)
            )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}