package com.example.pokedex.ui.fragment.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pokedex.data.model.response.Pokemon
import com.example.pokedex.databinding.ItemPokemonBinding
import com.example.pokedex.ui.fragment.home.viewholders.PokemonViewHolder

class PokemonAdapter(val pokemonSelected:(Pokemon)->Unit): ListAdapter<Pokemon, PokemonViewHolder> (POKEMON_COMPARATOR) {

    private var mLastClickTime = System.currentTimeMillis()
    private val CLICK_TIME_INTERVAL: Long = 300

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
        holder.setListener(item) { pokemon ->
            val now = System.currentTimeMillis()
            when (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                true -> { return@setListener }
                else -> { mLastClickTime = now; pokemonSelected(pokemon) }
            }
        }
    }

    /*override fun onCurrentListChanged(previousList: MutableList<Pokemon>, currentList: MutableList<Pokemon>) {
        super.onCurrentListChanged(previousList, currentList)
        if (currentList.isEmpty()) { pokemonEmpty(true) } else { pokemonEmpty(false) }
    }*/

    /*override fun onFilter(list: List<Pokemon>, constraint: String): List<Pokemon> {
        return list.filter { it.nombre.lowercase().contains(constraint.lowercase()) }
    }*/

    companion object {
        private val POKEMON_COMPARATOR = object : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean = oldItem == newItem
        }
    }


}