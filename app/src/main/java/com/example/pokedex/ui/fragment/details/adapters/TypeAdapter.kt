package com.example.pokedex.ui.fragment.details.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pokedex.data.model.pokemon.Types
import com.example.pokedex.databinding.ItemTypesBinding
import com.example.pokedex.ui.fragment.details.viewholders.TypesViewHolder

class TypeAdapter: ListAdapter<Types, TypesViewHolder>(TYPES_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypesViewHolder {
        val view = ItemTypesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TypesViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypesViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item, holder.itemView.context)
    }

    companion object {
        private val TYPES_COMPARATOR = object : DiffUtil.ItemCallback<Types>() {
            override fun areItemsTheSame(oldItem: Types, newItem: Types): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: Types, newItem: Types): Boolean = oldItem == newItem
        }
    }

}