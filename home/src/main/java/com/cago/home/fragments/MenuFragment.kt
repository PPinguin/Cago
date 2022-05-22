package com.cago.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cago.core.R
import com.cago.core.databinding.FragmentMenuBinding
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.core.models.Pack
import com.cago.home.activities.HomeActivity
import com.cago.home.adapters.MenuListAdapter
import com.cago.home.dialogs.alerts.PackDialog
import com.cago.home.dialogs.selections.EditPackDialog
import com.cago.home.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class MenuFragment : Fragment() {

    private var binding: FragmentMenuBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MenuListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as HomeActivity).viewModel
        adapter = MenuListAdapter(this::openPack, this::editPack)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater)
        // initialize UI
        binding?.let {
            it.list.adapter = adapter
            it.fabNew.setOnClickListener { createPack() }
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
            binding?.emptyList?.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(it.toMutableList())
        }
        return binding?.root
    }

    private fun editPack(pack: Pack) {
        EditPackDialog { label ->
            when (label) {
                getString(R.string.open) -> {
                    openPack(pack)
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
        val intent = Intent()
        val uri = viewModel.getPackUri(pack)
        intent.setDataAndType(uri, requireContext().contentResolver.getType(uri))
        requireActivity().startActivity(Intent.createChooser(intent, getString(R.string.share_pack)))
    }

    private fun openPack(pack: Pack) {
        (requireActivity() as HomeActivity).openPack(pack.name, pack.path)
    }

    private fun createPack() {
        PackDialog { name ->
            viewModel.createPack(name)
        }.show(requireActivity().supportFragmentManager, getString(R.string.new_pack))
    }

    private fun deletePack(pack: Pack) {
        QuestionDialog({
            viewModel.deletePack(pack)
        }, getString(R.string.question_delete, pack.name))
            .show(requireActivity().supportFragmentManager, getString(R.string.delete))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}