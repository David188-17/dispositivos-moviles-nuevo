package com.lugares_j.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AsyncPlayer
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.lugares_j.R
import com.lugares_j.databinding.FragmentUpdateLugarBinding
import com.lugares_j.model.Lugar

import com.lugares_j.viewmodel.LugarViewModel


class UpdateLugarFragment : Fragment() {

    private val args by navArgs<UpdateLugarFragmentArgs>()

    private lateinit var lugarViewModel: LugarViewModel
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)


        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLongitud.text = args.lugar.longitud.toString()
        binding.tvLatitud.text = args.lugar.latitud.toString()
        binding.tvAltura.text = args.lugar.altura.toString()

        binding.btUpdate.setOnClickListener { updateLugar() }
        binding.btDelete.setOnClickListener { deleteLugar() }

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        binding.btWhatsapp.setOnClickListener { enviarWhatsApp() }
        binding.btWeb.setOnClickListener { verWeb() }
        binding.btLocation.setOnClickListener { verEnMapa() }

        if (args.lugar.rutaAudio?.isNotEmpty()==true) {

            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.lugar.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled=true

        } else {
            binding.btPlay.isEnabled=false

        }

        binding.btPlay.setOnClickListener{mediaPlayer.start()}

        if (args.lugar.rutaImagen?.isNotEmpty()==true) {

            Glide.with(requireContext())
                .load(args.lugar.rutaImagen)
                .fitCenter()
                .into(binding.imagen)

        }

        return binding.root
    }

    private fun escribirCorreo() {

        val valor = binding.etCorreo.text.toString()
        if (valor.isNotEmpty()) {

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(valor))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos) + " " + binding.etNombre.text
            )

            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.msg_mensaje_correo)
            )

            startActivity(intent)

        } else {

            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG
            ).show()

        }

    }

    private fun llamarLugar() {

        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$valor")

            if (requireActivity()
                    .checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED
                && requireActivity()
                    .checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

                requireActivity().requestPermissions(
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ), 105
                )

            } else {
                requireActivity().startActivity(intent)

            }

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG
            ).show()

        }

    }

    private fun enviarWhatsApp() {

        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$valor&text=" +
                    getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)
            startActivity(intent)

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG
            ).show()

        }
    }

    private fun verWeb() {

        val valor = binding.etWeb.text.toString()
        if (valor.isNotEmpty()) {

            val uri = "http://$valor"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else {

            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG
            ).show()

        }
    }

    private fun verEnMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val lonitud = binding.tvLongitud.text.toString().toDouble()
        if (latitud.isFinite() && lonitud.isFinite()) {

            val uri = "geo: $latitud, $lonitud?z18"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else {

            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG
            ).show()

        }

    }

    private fun deleteLugar() {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle(R.string.bt_delete_lugar)
        alerta.setMessage(getString(R.string.msg_pregunta_delete) + " ${args.lugar.nombre}? ")
        alerta.setPositiveButton(getString(R.string.msg_si)) { _, _ ->
            lugarViewModel.deleteLugar(args.lugar)
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_deleted),
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

        }
        alerta.setNegativeButton(getString(R.string.msg_no)) { _, _ ->

        }
        alerta.create().show()
    }

    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()

        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val web = binding.etWeb.text.toString()
            val lugar = Lugar(
                args.lugar.id,
                nombre,
                correo,
                telefono,
                web,
                args.lugar.latitud,
                args.lugar.longitud,
                args.lugar.altura,
                args.lugar.rutaAudio,
                args.lugar.rutaImagen
            )

            lugarViewModel.saveLugar(lugar)
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_updated),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

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