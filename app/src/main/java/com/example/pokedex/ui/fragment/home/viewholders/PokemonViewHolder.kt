package com.example.pokedex.ui.fragment.home.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.data.model.response.Pokemon
import com.example.pokedex.databinding.ItemPokemonBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class PokemonViewHolder(private val viewBinding: ItemPokemonBinding): RecyclerView.ViewHolder(viewBinding.root) {

    private var mLastClickTime = System.currentTimeMillis()
    private val CLICK_TIME_INTERVAL: Long = 300

    fun bind(pokemon: Pokemon/*, context: Context*/) {
        //viewBinding.root.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in))
        val index = pokemon.url.split("/".toRegex()).dropLast(1).last()
        viewBinding.name.text = pokemon.name.uppercase()

        Picasso
            .get()
            .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png")
            .into(viewBinding.photo, object : Callback {
                override fun onSuccess() {
                    viewBinding.photo.visibility = View.VISIBLE
                    /*viewBinding.progress.visibility = View.GONE*/
                }
                override fun onError(e: Exception?) { }
            })



        /*viewBinding.nombre.setTextAnimation(data.nombre)
        viewBinding.fechaInicio.setTextAnimation(returnTime(data.fecha_inicio))
        viewBinding.fechaFin.setTextAnimation(returnTime(data.fecha_fin))*/
    }

    fun setListener(pokemon: Pokemon, pokemonSelected:(Pokemon) -> Unit) {
        with(viewBinding.root) {
            setOnClickListener {pokemonSelected(pokemon)
                /*val now = System.currentTimeMillis()
                when (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    true -> { return@setOnClickListener }
                    else -> { mLastClickTime = now; pokemonSelected(pokemon) }
                }*/
            }
        }
    }


    /*fun getImageUrl(): String {
        val index = url.split("/".toRegex()).dropLast(1).last()
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png"
    }*/

}