package com.example.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.model.pokemon.PokemonInfo
import com.example.pokedex.data.model.response.Response
import com.example.pokedex.data.repository.PokedexRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val repo: PokedexRepositoryImpl) : ViewModel() {

    fun getPokemons(
        onSuccess: (Response) -> Unit,
        onFailure: (Int, String) -> Unit,
        onConnectionFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = repo.getPokemons().awaitResponse()
                if (result.isSuccessful && result.body() != null) {
                    val response = result.body()!!
                    onSuccess(response)
                } else {
                    onFailure(result.code(), result.message())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onConnectionFailure("${e.message}")
            }
        }
    }

    fun getInfoPokemon(
        idPokemon: Int,
        onSuccess: (PokemonInfo) -> Unit,
        onFailure: (Int, String) -> Unit,
        onConnectionFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = repo.getInfoPokemon(idPokemon).awaitResponse()
                if (result.isSuccessful && result.body() != null) {
                    val response = result.body()!!
                    onSuccess(response)
                } else {
                    onFailure(result.code(), result.message())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onConnectionFailure("${e.message}")
            }
        }
    }


}