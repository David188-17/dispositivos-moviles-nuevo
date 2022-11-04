package com.lugares_j.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

import com.lugares_j.model.Lugar


class LugarDao {

    //variables usadas para poder generar la estructura en la nube

    private val coleccion1 = "lugaresApp"
   private val usuario = Firebase.auth.currentUser?.email.toString()
   private val coleccion2 = "mislugares"

    //obtiene la conexion a la base de datos
    private var firestore: FirebaseFirestore =FirebaseFirestore.getInstance()

    init{
        //inicia la configuracion de firestore
        firestore.firestoreSettings =FirebaseFirestoreSettings.Builder().build()


    }
     fun saveLugar(lugar: Lugar){
         //Para definir un documento en la nube

val documento : DocumentReference

if (lugar.id.isEmpty()) {//si esta vacio es un nuevo documento...
documento =firestore.

collection(coleccion1)

    .document(usuario)
    .collection(coleccion2)
    .document()
    lugar.id = documento.id

}else{//si el id tiene algo... entonces se va a modificar ese documento(lugar)
    documento =firestore.

    collection(coleccion1)

        .document(usuario)
        .collection(coleccion2)
        .document()


}
         //ahora se modifica o crea el documento
         documento.set(lugar)
             .addOnCompleteListener{
                 Log.d("saveLugar","Lugar creado/actualizado")
             }
             .addOnCompleteListener{
                 Log.e("saveLugar","Lugar no crwado/actualizado")
             }
    }





     fun deleteLugar(lugar: Lugar) {
         // se valida si el lugar tiene id... para poder borrarlo
         if (lugar.id.isEmpty()) {//si  no esta vacio ... se puede eliminar
             firestore.
             collection(coleccion1)

                 .document(usuario)
                 .collection(coleccion2)
                 .document(lugar.id)

                 .delete()

                 .addOnCompleteListener{
                     Log.d("deleteLugar","Lugar eliminado")
                 }
                 .addOnCompleteListener{
                     Log.e("deleteLugar","Lugar No eliminado")
                 }
         }
     }

    fun getLugares() : MutableLiveData<List<Lugar>> {
val listaLugares = MutableLiveData<List<Lugar>>()

        firestore.
        collection(coleccion1)

            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener { instantanea, e ->
                if (e != null) {//se dio un error ....capturando la imagen de info
                    return@addSnapshotListener

                }
                //si estamos aca .. no hubo error..
                if (instantanea != null) { //si se pudo recuperar la info...
                    val lista = ArrayList<Lugar>()
// se recorre a instantanea documento por documento... convirtiendolo en Lugar y agregandolo en la lista
                    instantanea.documents.forEach {
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar != null) {// si se pudo convertir el documento en un lugar
                            lista.add(lugar) //se agrega el lugar a la lista...


                        }
                    }
                    listaLugares.value = lista
                }
            }
        return listaLugares


    }

}