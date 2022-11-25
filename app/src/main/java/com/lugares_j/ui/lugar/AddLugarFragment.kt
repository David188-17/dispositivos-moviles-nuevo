package com.lugares_j.ui.lugar

import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles

import com.lugares_j.R
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.model.Lugar

import com.lugares_j.viewmodel.LugarViewModel


class AddLugarFragment : Fragment() {


    private lateinit var lugarViewModel: LugarViewModel
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var audioUtiles: AudioUtiles
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAdd.setOnClickListener { subeNota() }
        binding.progressBar.visibility = ProgressBar.VISIBLE
        binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
        binding.msgMensaje.visibility= TextView.VISIBLE
        activaGPS()

        audioUtiles = AudioUtiles(
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio)



        )

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()

            }

        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)


        return binding.root

    }


    private fun subeNota() {

        val archivoLocal = audioUtiles.audioFile
        if(archivoLocal.exists() && archivoLocal.isFile &&
            archivoLocal.canRead()) {

            val rutaLocal = Uri.fromFile(archivoLocal)

            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${archivoLocal.name}"

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referencia.downloadUrl
                        .addOnSuccessListener {
                            val rutaAudio = it.toString()
                            subeImagen(rutaAudio)
                        }
                }
                .addOnFailureListener{

                    subeImagen("")

                }
        } else {

            subeImagen("")

        }
    }

    private fun subeImagen(rutaAudio: String) {

        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)

        val archivoLocal = imagenUtiles.imagenFile
        if(archivoLocal.exists() && archivoLocal.isFile &&
            archivoLocal.canRead()) {

            val rutaLocal = Uri.fromFile(archivoLocal)

            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/imagenes/${archivoLocal.name}"

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referencia.downloadUrl
                        .addOnSuccessListener {
                            val rutaImagen = it.toString()
                            addLugar(rutaAudio,rutaImagen)
                        }
                }
                .addOnFailureListener{

                    addLugar(rutaAudio,"")

                }
        } else {

            addLugar(rutaAudio,"")

        }

    }


    private fun activaGPS() {

        if (requireActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
            && requireActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            requireActivity().requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), 105)

        } else{

            val ubicacion: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            ubicacion.lastLocation.addOnSuccessListener {
                    location: Location? ->
                if (location != null) {
                    binding.tvLatitud.text = "${location.latitude}"
                    binding.tvLongitud.text = "${location.longitude}"
                    binding.tvAltura.text = "${location.altitude}"

                } else {
                    binding.tvLatitud.text = "0.0"
                    binding.tvLongitud.text = "0.0"
                    binding.tvAltura.text = "0.0"


                }

            }
        }

    }

    private fun addLugar(rutaAudio: String, rutaImagen: String) {
        val nombre = binding.etNombre.text.toString()

        binding.msgMensaje.text = getString(R.string.msg_subiendo_lugar)
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val web = binding.etWeb.text.toString()
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("", nombre, correo, telefono, web,
                latitud, longitud, altura, rutaAudio, rutaImagen)

            lugarViewModel.saveLugar(lugar)
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_added),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}