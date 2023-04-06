package com.example.pokedex.ui.fragment.details.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.R
import com.example.pokedex.data.model.pokemon.Types
import com.example.pokedex.databinding.ItemTypesBinding

class TypesViewHolder(private val viewBinding: ItemTypesBinding): RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(type: Types, context: Context) {
        viewBinding.type.text = type.type.name

        when (type.type.name) {
            "fighting" -> typeColor(R.color.fighting,context)
            "flying" -> typeColor(R.color.flying, context)
            "poison" -> typeColor(R.color.poison, context)
            "ground" -> typeColor(R.color.ground, context)
            "rock" -> typeColor(R.color.rock, context)
            "bug" -> typeColor(R.color.bug, context)
            "ghost" -> typeColor(R.color.ghost, context)
            "steel" -> typeColor(R.color.steel, context)
            "fire" -> typeColor(R.color.fire, context)
            "water" -> typeColor(R.color.water, context)
            "grass" -> typeColor(R.color.grass, context)
            "electric" -> typeColor(R.color.electric, context)
            "psychic" -> typeColor(R.color.psychic, context)
            "ice" -> typeColor(R.color.ice, context)
            "dragon" -> typeColor(R.color.dragon, context)
            "fairy" -> typeColor(R.color.fairy, context)
            "dark" -> typeColor(R.color.dark, context)
            else -> typeColor(R.color.gray_21, context)
        }

    }

    private fun typeColor(color: Int, context: Context) {
        viewBinding.parentCard.setBackgroundResource(color)
    }



}