package com.cago.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cago.core.R
import com.cago.core.databinding.ItemMenuBinding
import com.cago.core.models.Pack

class MenuListAdapter(
    val onClick: (Pack) -> Unit,
    val onActionClick: (Pack) -> Unit
) :
    ListAdapter<Pack, MenuListAdapter.Item>(
        object : DiffUtil.ItemCallback<Pack>() {
            override fun areItemsTheSame(oldItem: Pack, newItem: Pack): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: Pack, newItem: Pack): Boolean =
                oldItem == newItem
        }
    ) {

    inner class Item(private val binding: ItemMenuBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(p: Pack) {
            binding.apply {
                name.text = p.name
                uploaded = p.key != null
                actual = p.actual
                action.setOnClickListener { onActionClick(p) }
                root.setOnClickListener { onClick(p) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        val binding = DataBindingUtil.inflate<ItemMenuBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_menu,
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