package com.example.pokedex.ui.dialog

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentAlertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertFragment : DialogFragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAlertBinding.inflate(layoutInflater, container, false)
        val window: Window? = dialog?.window
        window?.setBackgroundDrawableResource(R.color.transparent)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        getOrigen()
    }

    private fun getOrigen() {
        arguments.let { bundle ->
            if ((bundle != null) && (bundle.containsKey("origen"))) {
                val origen = bundle.getString("origen")
                when(origen) {
                    "onRational" -> { onRational() }
                    "onDenied" -> { onDenied() }
                    "onFailure" -> { onFailure() }
                    "onConnectionFailure" -> { onConnectionFailure() }
                    "onGPS" -> { onGPS() }
                }
            }
        }
    }

    private fun onGPS() {
        binding.animationView.setAnimation(R.raw.pikachu)
        binding.code.text = "GPS Desactivado"
        binding.message.text = "Se requiere de la activación de GPS"
        binding.materialButton2.setOnClickListener {
            val bundle = bundleOf("resultado" to true)
            setFragmentResult("requestKey", bundle)
            dismiss()
        }
    }

    private fun onConnectionFailure() {
        binding.animationView.setAnimation(R.raw.connection_error)
        binding.code.text = "¡Error de conexión!"
        binding.message.text = "Hubo un fallo en la conexión. \n Vuelve a intentarlo."
    }

    private fun onFailure() {
        binding.animationView.setAnimation(R.raw.error)
    }

    private fun onDenied() {
        binding.animationView.setAnimation(R.raw.permission)
        binding.code.text = "Has negado todos los permisos"
        binding.message.text = "Se han negado todos los permisos necesarios para funcionar correctamente la aplicación móvil. Por lo tanto se le redirigira a las configuraciones para que pueda aceptarlas."
        binding.materialButton2.setOnClickListener {
            val bundle = bundleOf("resultado" to true)
            setFragmentResult("requestKey", bundle)
            dismiss()
        }
    }

    private fun onRational() {
        binding.animationView.setAnimation(R.raw.alert)
        binding.code.text = "Se requieren permisos"
        binding.message.text = "Se requieren los permisos necesarios para poder utilizar correctamente la aplicación móvil."
    }

    private fun onClick() { binding.materialButton2.setOnClickListener { dismiss() } }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}