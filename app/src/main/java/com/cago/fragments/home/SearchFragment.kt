package com.cago.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.cago.activities.HomeActivity
import com.cago.adapters.lists.SearchListAdapter
import com.cago.databinding.FragmentSearchBinding
import com.cago.models.server.PackInfo
import com.cago.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (requireActivity() as HomeActivity).viewModel
        adapter = SearchListAdapter(this::openPack)
    }

    private fun openPack(info: PackInfo) {
        (requireActivity() as HomeActivity)
            .openPack(info.name!!, bundleOf("path" to info.path))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        binding?.let {
            it.results.adapter = adapter
            it.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.updateSearchResults(query!!)
                    it.refresh.isRefreshing = true
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean = false
            })
            it.refresh.setOnRefreshListener {
                with(it.searchView.query) {
                    if (isNotEmpty()) viewModel.updateSearchResults(this.toString())
                }
            }
            it.searchView.isIconified = false
        }
        viewModel.searchLiveData.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding?.let {
                it.refresh.isRefreshing = false
                it.img.visibility = if (list.isEmpty())
                    View.VISIBLE
                else
                    View.GONE
            }
        }
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                binding?.let {
                    Snackbar.make(it.root, msg, Snackbar.LENGTH_SHORT).show()
                }
                viewModel.message.postValue("")
            }
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.searchLiveData.postValue(emptyList())
    }
}