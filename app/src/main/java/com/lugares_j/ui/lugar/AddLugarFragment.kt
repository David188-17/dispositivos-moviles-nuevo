package com.lugares_j.ui.lugar

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.lugares_j.R
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.databinding.FragmentLugarBinding
import com.lugares_j.model.Lugar
import com.lugares_j.viewmodel.LugarViewModel

class AddLugarFragment : Fragment() {

    //El objeto para interactuar finalmente con la tabla...
    private lateinit var lugarViewModel: LugarViewModel

    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAdd.setOnClickListener { addLugar() }

        activaGPS()




        return binding.root
    }

    private fun activaGPS() {
        if (requireActivity()
                .checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
            && requireActivity()
                .checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            //si estamos aca hay que pedir autorizacion para hacer la ubicacion gps
            requireActivity()
                .requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION), 105)

        }else{
            //si se tienen los permisos se busca la ubicacion gps
val ubicacion:FusedLocationProviderClient =

    LocationServices.getFusedLocationProviderClient(requireContext())
         ubicacion.lastLocation.addOnSuccessListener {
             location: Location? ->
             if(location!=null){
                 binding.tvLatitud.text="${location.latitude}"
                 binding.tvLongitud.text="${location.longitude}"
                 binding.tvAltura.text="${location.altitude}"
             }else{
                 binding.tvLatitud.text ="0.0"
                 binding.tvLongitud.text="0.0"
                 binding.tvAltura.text ="0.0"

             }
         }

    }
    }

    private fun addLugar() {
        val nombre = binding.etNombre.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
        if (nombre.isNotEmpty()) {  //Si se escribió algo en el nombre se puede guardar el lugar
            val correo = binding.etCorreo.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
            val telefono = binding.etTelefono.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
            val web = binding.etWeb.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
          val latitud =binding.tvLatitud.text.toString().toDouble()
          val longitud =binding.tvLongitud.text.toString().toDouble()
          val Altura =binding.tvAltura.text.toString().toDouble()

            val lugar = Lugar("",nombre,correo,telefono,web,
                latitud,longitud,Altura,"","")

            //Se procede a registrar el nuevo lugar..............
            lugarViewModel.saveLugar(lugar)
            Toast.makeText(requireContext(),
                getString(R.string.msg_lugar_added),
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {  //No se puede registrar el lugar... falta info
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}