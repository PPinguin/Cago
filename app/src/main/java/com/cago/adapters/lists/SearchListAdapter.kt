package com.cago.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cago.R
import com.cago.databinding.ItemSearchBinding
import com.cago.models.server.PackInfo

class SearchListAdapter(
    val open: (PackInfo) -> Unit
) :
    ListAdapter<PackInfo, SearchListAdapter.Item>(
        object : DiffUtil.ItemCallback<PackInfo>() {
            override fun areItemsTheSame(oldItem: PackInfo, newItem: PackInfo): Boolean =
                oldItem.name == newItem.name && oldItem.path == newItem.path

            override fun areContentsTheSame(oldItem: PackInfo, newItem: PackInfo): Boolean =
                oldItem == newItem
        }
    ) {
    inner class Item(private val binding: ItemSearchBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(i: PackInfo) {
            binding.apply {
                info = i
                open.setOnClickListener { open(i) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        val binding = DataBindingUtil.inflate<ItemSearchBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_search,
            parent,
            false
        )
        return Item(binding)
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size
}