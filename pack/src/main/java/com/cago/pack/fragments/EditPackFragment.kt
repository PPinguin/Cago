package com.cago.pack.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cago.core.R
import com.cago.core.databinding.FragmentEditPackBinding
import com.cago.pack.activities.PackActivity
import com.cago.pack.adapters.EditPackFragmentsAdapter
import com.cago.pack.dialogs.alerts.InputDialog
import com.cago.pack.dialogs.alerts.OutputDialog
import com.cago.pack.dialogs.info.InfoEditDialog
import com.cago.pack.fragments.edit.EditPackageFragment
import com.cago.pack.viewmodels.PackViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class EditPackFragment : Fragment() {

    private var binding: FragmentEditPackBinding? = null
    private lateinit var adapter: EditPackFragmentsAdapter
    private lateinit var viewModel: PackViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as PackActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPackBinding.inflate(inflater)
        adapter = EditPackFragmentsAdapter(this)
        // initialize UI
        binding?.let {
            it.pager.adapter = adapter
            it.toolbar.apply {
                title = viewModel.pack.value
                inflateMenu(R.menu.pack_menu)
                overflowIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_options)
                setNavigationIcon(R.drawable.ic_less)
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.info -> {
                            editInfo()
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }
            }
            TabLayoutMediator(it.tabs, it.pager){ tab, position ->
                tab.text = if (position % 2 == 0) getString(R.string.input)
                else getString(R.string.output)
            }.attach()
            viewModel.message.observe(viewLifecycleOwner){ msg ->
                if(msg.isNotEmpty()) {
                    Snackbar.make(it.root, msg, Snackbar.LENGTH_SHORT).show()
                    viewModel.message.value = ""
                }
            }
            it.fab.setOnClickListener { _ -> 
                getEditFragment(it.pager.currentItem).createField() 
            }
            it.e.setOnClickListener { _ ->
                getEditFragment(it.pager.currentItem).editField()
            }
            it.c.setOnClickListener { _ ->
                getEditFragment(it.pager.currentItem).copyField()
            }
            it.v.setOnClickListener { _ ->
                getEditFragment(it.pager.currentItem).valueField()
            }
            it.d.setOnClickListener { _ ->
                getEditFragment(it.pager.currentItem).deleteField()
            }
        }
        return binding?.root
    }
    
    private fun getEditFragment(i: Int): EditPackageFragment =
        if(i == 0) EditPackFragmentsAdapter.inputFragment
        else EditPackFragmentsAdapter.outputFragment

    private fun editInfo() {
        InfoEditDialog(viewModel.getDescription()) { text ->
            viewModel.setDescription(text)
        }.show(
            requireActivity().supportFragmentManager,
            getString(R.string.description)
        )
    }

    override fun onDestroyView() {
        binding = null
        viewModel.run()
        super.onDestroyView()
    }
}