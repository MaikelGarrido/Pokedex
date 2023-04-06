package com.example.pokedex.data.model.response

data class Response(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Pokemon>
)