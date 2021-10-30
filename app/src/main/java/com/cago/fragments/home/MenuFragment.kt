package com.cago.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cago.R
import com.cago.activities.HomeActivity
import com.cago.adapters.lists.MenuListAdapter
import com.cago.databinding.FragmentMenuBinding
import com.cago.dialogs.alerts.PackDialog
import com.cago.dialogs.alerts.QuestionDialog
import com.cago.dialogs.selections.EditPackDialog
import com.cago.models.Pack
import com.cago.utils.ErrorType
import com.cago.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class MenuFragment : Fragment() {

    private var binding: FragmentMenuBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MenuListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as HomeActivity).viewModel
        adapter = MenuListAdapter(requireContext(), this::openPack, this::editPack)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater)
        // initialize UI
        binding?.let {
            it.list.adapter = adapter
            it.toolbar.inflateMenu(R.menu.home_menu)
            it.toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search -> {
                        searchPack()
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            it.fabNew.setOnClickListener { createPack() }
            it.newPack.setOnClickListener { createPack() }
        }
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                binding?.let {
                    Snackbar.make(it.root, msg, Snackbar.LENGTH_SHORT).show()
                }
                viewModel.message.postValue("")
            }
        }
        viewModel.listLiveData.observe(viewLifecycleOwner) {
            binding?.newPack?.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(it.toMutableList())
        }
        return binding?.root
    }

    private fun searchPack() {
        findNavController().navigate(R.id.action_menuFragment_to_searchFragment)
    }

    private fun editPack(pack: Pack) {
        EditPackDialog { label ->
            when (label) {
                getString(R.string.open) -> {
                    openPack(pack)
                }
                getString(R.string.upload) -> {
                    uploadPack(pack)
                }
                getString(R.string.share) -> {
                    sharePack(pack)
                }
                getString(R.string.delete) -> {
                    deletePack(pack)
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.edit_pack))
    }

    private fun sharePack(pack: Pack) {
        if (pack.key == null) {
            binding?.let {
                Snackbar.make(
                    it.root,
                    getString(ErrorType.ERROR_SHARE.getResource(), pack.name),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            val shareIntent = Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, viewModel.generateUri(pack))
                    type = "text/plain"
                }, null
            )
            requireActivity().startActivityFromFragment(this, shareIntent, 0)
        }
    }

    private fun openPack(pack: Pack) {
        (requireActivity() as HomeActivity).openPack(pack.name)
    }

    private fun createPack() {
        PackDialog({ name ->
            viewModel.createPack(name)
        }).show(requireActivity().supportFragmentManager, getString(R.string.new_pack))
    }

    private fun deletePack(pack: Pack) {
        QuestionDialog({
            viewModel.deletePack(pack)
        }, getString(R.string.question_delete, pack.name))
            .show(requireActivity().supportFragmentManager, getString(R.string.delete))
    }

    private fun uploadPack(pack: Pack) {
        viewModel.uploadPack(pack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}