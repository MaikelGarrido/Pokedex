package com.example.pokedex.ui.fragment.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokedex.data.model.response.Pokemon
import com.example.pokedex.data.services.TrackingService
import com.example.pokedex.databinding.FragmentHomeBinding
import com.example.pokedex.ui.activity.MainActivity
import com.example.pokedex.ui.dialog.AlertFragment
import com.example.pokedex.ui.fragment.home.adapters.PokemonAdapter
import com.example.pokedex.utils.PermissionRequesterFragment
import com.example.pokedex.utils.checkLocationPermission
import com.example.pokedex.utils.openAppOptions
import com.example.pokedex.viewmodel.ViewModel
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var bm: LocalBroadcastManager? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModel by viewModels()
    private val pokemonAdapter: PokemonAdapter by lazy {
        PokemonAdapter(pokemonSelected = { pokemon ->
            pokemonSelected(
                pokemon
            )
        })
    }
    private val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionTiramissu = arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val permissions = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val permissionRequester = PermissionRequesterFragment(
        fragment = this,
        permissionsGlobal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissionTiramissu else permissions,
        onRational = { onRational() },
        onDenied = { onDenied() }
    )

    private var activityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            /*if (result.resultCode == Activity.RESULT_OK) { }*/
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onGPS()
            } else {
                setupStartService()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionRequester.runWithPermissions { setupService() }
        setupSearchPokemon()
        setupRecyclerView()
        setupGetPokemons()
    }

    private fun setupSearchPokemon() {
        binding.searchPokemon.setOnClickListener {
            if (pokemonAdapter.currentList.isNotEmpty()) {
                val pokemon = pokemonAdapter.currentList.random()
                pokemonSelected(pokemon)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvPokemons.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = pokemonAdapter
        }
    }

    private fun setupService() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS()
        } else {
            setupStartService()
        }
    }

    private fun setupStartService() {
        if (requireContext().checkLocationPermission()) {
            requireActivity().startService(Intent(requireContext(), TrackingService::class.java))
        } else {
            /*permissionRequester.runWithPermissions { setupStartService() }*/
        }
    }

    private fun setupStopService() {
        requireActivity().stopService(Intent(requireContext(), TrackingService::class.java))
    }

    private fun setupGetPokemons() {
        viewModel.getPokemons(
            onSuccess = { response ->
                if (response.results.isNotEmpty()) {
                    pokemonAdapter.submitList(response.results)
                    binding.searchPokemon.visibility = View.VISIBLE
                }
            },
            onFailure = { code, message -> },
            onConnectionFailure = { message -> }
        )
    }

    private fun pokemonSelected(pokemon: Pokemon) {
        setupStopService()
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToPokemonDetailsFragment(
                pokemon
            )
        )
    }

    private fun onRational() {
        val dialog = AlertFragment()
        val bundle = Bundle()
        bundle.putString("origen", "onRational")
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "")
    }

    private fun onDenied() {
        val dialog = AlertFragment()
        val bundle = Bundle()
        bundle.putString("origen", "onDenied")
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "")
        dialog.setFragmentResultListener("requestKey") { key, bundle ->
            val result = bundle.getBoolean("resultado")
            if (result) {
                requireContext().openAppOptions()
            }
        }
    }

    private fun onGPS() {
        val dialog = AlertFragment()
        val bundle = Bundle()
        bundle.putString("origen", "onGPS")
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "")
        dialog.setFragmentResultListener("requestKey") { key, bundle ->
            val result = bundle.getBoolean("resultado")
            if (result) {
                activityResultLauncher.launch(intent)
            }
        }
    }

    private val onJsonReceived = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && pokemonAdapter.currentList.isNotEmpty()) {
                setupNotification()
                val pokemon = pokemonAdapter.currentList.random()
                pokemonSelected(pokemon)
            }
        }
    }

    private fun setupNotification() {
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(
                "NOTIFICATION_CHANNEL_ID"
            ) == null
        ) {
            val channel = NotificationChannel(
                "NOTIFICATION_CHANNEL_ID",
                getString(com.example.pokedex.R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val stackBuilder = TaskStackBuilder.create(context).addParentStack(MainActivity::class.java).addNextIntent(intent)
        val notificationPendingIntent = stackBuilder.getPendingIntent(getUniqueId(), FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(requireContext(), "NOTIFICATION_CHANNEL_ID")
            .setSmallIcon(com.example.pokedex.R.mipmap.ic_launcher_round)
            .setContentTitle("Alerta")
            .setContentText("Pokemon encontrado")
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getUniqueId(), notification)
    }

    private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bm = LocalBroadcastManager.getInstance(requireContext())
        val actionReceiver = IntentFilter()
        actionReceiver.addAction("nameOfTheAction")
        bm?.registerReceiver(onJsonReceived, actionReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        bm?.unregisterReceiver(onJsonReceived);
    }

    override fun onDestroy() {
        super.onDestroy()
        setupStopService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setupStopService()
        _binding = null
    }


}

