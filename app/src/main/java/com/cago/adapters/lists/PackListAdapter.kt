package com.cago.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cago.R
import com.cago.adapters.lists.diffs.FieldDiffCallback
import com.cago.databinding.ItemPackBinding
import com.cago.models.logic.Field

class PackListAdapter(private val listener: ((Int) -> Unit)? = null) :
    ListAdapter<Field, PackListAdapter.Item>(
        AsyncDifferConfig.Builder(FieldDiffCallback()).build()
    ) {

    inner class Item(val binding: ItemPackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                field = currentList[position]
                listener?.let { l -> root.setOnClickListener { l(position) } }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pack,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = currentList.size
}