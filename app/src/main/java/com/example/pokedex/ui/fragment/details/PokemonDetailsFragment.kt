package com.example.pokedex.ui.fragment.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedex.R
import com.example.pokedex.data.model.pokemon.PokemonInfo
import com.example.pokedex.databinding.FragmentPokemonDetailsBinding
import com.example.pokedex.ui.fragment.details.adapters.TypeAdapter
import com.example.pokedex.utils.getHeightString
import com.example.pokedex.utils.getWeightString
import com.example.pokedex.viewmodel.ViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PokemonDetailsFragment : Fragment() {

    private var _binding : FragmentPokemonDetailsBinding? = null
    private val binding get() = _binding!!

    private val typeAdapter: TypeAdapter by lazy { TypeAdapter() }
    private val navArgs: PokemonDetailsFragmentArgs by navArgs()
    private val viewmodel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPokemonDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupInfoPokemon()
        setupImage()
    }

    private fun setupInfoPokemon() {
        viewmodel.getInfoPokemon(
            idPokemon = navArgs.pokemon.url.split("/".toRegex()).dropLast(1).last().toInt(),
            onSuccess = { pokemonInfo -> setupPokemon(pokemonInfo) },
            onFailure = { code, message -> },
            onConnectionFailure = { message -> }
        )
    }

    private fun setupRecyclerView() {
        binding.rvType.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            isNestedScrollingEnabled = false
            adapter = typeAdapter
        }
    }

    private fun setupPokemon(pokemonInfo: PokemonInfo) {
        binding.name.text = navArgs.pokemon.name.uppercase()
        binding.height.text = getHeightString(pokemonInfo.height)
        binding.weight.text = getWeightString(pokemonInfo.weight)

        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Column)
            .backgroundColor(R.color.background)
            .axesTextColor("#fff")
            .polar(true)
            .dataLabelsEnabled(false)
            .categories(pokemonInfo.stats.map { it.stat.name }.toTypedArray())
            .series(arrayOf(
                AASeriesElement()
                    .name("Estadísticas de ${navArgs.pokemon.name.uppercase()}")
                    .colorByPoint(true)
                    .data(pokemonInfo.stats.map { it.base_stat }.toTypedArray())
            ))
        binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)

        typeAdapter.submitList(pokemonInfo.types)
    }

    private fun setupImage() {
        val index = navArgs.pokemon.url.split("/".toRegex()).dropLast(1).last()
        Picasso
            .get()
            .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png")
            .into(binding.image, object : Callback {
                override fun onSuccess() {
                    binding.image.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE

                    val palette = Palette.from(binding.image.drawable.toBitmap()).generate()
                    palette.apply {
                        vibrantSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }

                        darkVibrantSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }

                        lightVibrantSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }

                        lightMutedSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }
                        mutedSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }

                        darkMutedSwatch?.let {
                            binding.header.setBackgroundColor(it.rgb)
                            requireActivity().window.statusBarColor = it.rgb
                        }

                    }

                }
                override fun onError(e: Exception?) { }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}