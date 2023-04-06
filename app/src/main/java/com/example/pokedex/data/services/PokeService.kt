package com.example.pokedex.data.services

import com.example.pokedex.data.model.pokemon.PokemonInfo
import com.example.pokedex.data.model.response.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeService {

    @GET("pokemon/?limit=20&offset=0")
    fun getPokemons() : Call<Response>

    @GET("pokemon/{id}")
    fun getInfoPokemon(@Path("id") idPokemon:Int) :Call<PokemonInfo>

}