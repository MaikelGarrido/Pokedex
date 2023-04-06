package com.example.pokedex.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
) : Parcelable