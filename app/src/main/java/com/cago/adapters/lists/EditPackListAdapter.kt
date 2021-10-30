package com.cago.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cago.R
import com.cago.adapters.lists.diffs.FieldDiffCallback
import com.cago.databinding.ItemEditBinding
import com.cago.models.logic.Field

class EditPackListAdapter(
    private val onClick:(Int)->Unit,
    private var selected: Int
    ) :
    ListAdapter<Field, EditPackListAdapter.Item>(FieldDiffCallback()) {
    
    inner class Item(val binding: ItemEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(f: Field) {
            binding.apply {
                this.field = f
                this.active = selected == layoutPosition
                id.text = (layoutPosition + 1).toString()
                root.setOnClickListener { 
                    onClick(layoutPosition)
                    selectItem(layoutPosition)
                } 
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_edit,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = position
    
    fun selectItem(position: Int){
        notifyItemChanged(selected)
        selected = position
        notifyItemChanged(selected)
    }
}