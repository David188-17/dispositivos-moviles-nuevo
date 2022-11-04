package com.lugares_j.ui.lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        return binding.root
    }

    private fun addLugar() {
        val nombre = binding.etNombre.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
        if (nombre.isNotEmpty()) {  //Si se escribió algo en el nombre se puede guardar el lugar
            val correo = binding.etCorreo.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
            val telefono = binding.etTelefono.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
            val web = binding.etWeb.text.toString()  //Obtiene el texto de lo que el usuario escribió ahi..
            val lugar = Lugar("",nombre,correo,telefono,web,
                0.0,0.0,0.0,"","")

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