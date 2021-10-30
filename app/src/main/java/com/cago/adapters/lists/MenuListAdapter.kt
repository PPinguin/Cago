package com.cago.adapters.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cago.R
import com.cago.databinding.ItemMenuBinding
import com.cago.models.Pack

class MenuListAdapter(
    val context: Context,
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