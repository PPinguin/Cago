package com.cago.pack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cago.core.R
import com.cago.core.models.logic.Field

class FieldsChoiceAdapter(
    private val list: List<Field>, private val listener:(String) -> Unit)
    : RecyclerView.Adapter<FieldsChoiceAdapter.FieldChoice>(){
    inner class FieldChoice(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(name: String){
            itemView.findViewById<TextView>(R.id.name).text = name
            itemView.setOnClickListener { listener(name) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldChoice {
        return FieldChoice(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_field_choice, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FieldChoice, position: Int) {
        holder.bind(list[position].name)
    }

    override fun getItemCount(): Int = list.size
}