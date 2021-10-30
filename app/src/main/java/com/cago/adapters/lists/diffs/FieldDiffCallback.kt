package com.cago.adapters.lists.diffs

import androidx.recyclerview.widget.DiffUtil
import com.cago.models.logic.Field

class FieldDiffCallback : DiffUtil.ItemCallback<Field>() {

    override fun areItemsTheSame(oldItem: Field, newItem: Field): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: Field, newItem: Field): Boolean =
        oldItem.equal(newItem)
}