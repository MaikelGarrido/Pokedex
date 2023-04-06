package com.example.pokedex.data.repository

import com.example.pokedex.data.model.pokemon.PokemonInfo
import com.example.pokedex.data.model.response.Response
import com.example.pokedex.data.services.PokeService
import retrofit2.Call
import javax.inject.Inject

class PokedexRepositoryImpl @Inject constructor(val api: PokeService): PokeService {

    override fun getPokemons(): Call<Response> = api.getPokemons()
    override fun getInfoPokemon(idPokemon: Int): Call<PokemonInfo> = api.getInfoPokemon(idPokemon)


}