package com.example.pokedex.data.model.pokemon

import com.google.gson.annotations.SerializedName

data class PokemonInfo(
    @SerializedName("types")
    val types:List<Types>,

    @SerializedName("height")
    val height: Int,

    @SerializedName("weight")
    val weight: Int,

    val stats: List<Stats>
)


data class Types(
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("type")
    val type: Type
)

data class Type(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)

data class Stats(
    @SerializedName("base_stat")
    val base_stat: Int,

    @SerializedName("effort")
    val effort: Int,

    @SerializedName("stat")
    val stat: Stat
)

data class Stat(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)
