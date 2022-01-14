package com.util.parejasdepadel

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.util.*

/*
Clase creada para gestionar desde un único archivo la gestión de jugadores en el archivo txt
 */
class ConexionTXT(private val contextWrapper: Context) {

    // convertimos el contenido del archivo txt en el objeto jugadores
    fun obtenerJugadoresGuardados(): Jugadores {
        val cadena = obtenerTextoArchivo()
        val array = arrayListOf<Jugador>()
        if (cadena.isNotEmpty()) {
            // Separamos las cadenas de texto que van a representar a cada jugador
            val jugadores = cadena.split(';')
            for (jugador in jugadores) {
                // Este control lo hago porque siempre deja un último elemento vacío con el split
                if (jugador.isNotEmpty()){
                    val datos = jugador.split(',')
                    val nombre = datos[0]
                    val nivel: Int = datos[1].toInt()
                    val jugadorGuardado = Jugador (nombre, nivel)
                    array.add(jugadorGuardado)
                }
            }
        }
        return Jugadores(array)
    }

    //  sobre los jugadores guardados en el archivo, reocrremos todos hasta encontrar al que
    //  queremos eliminar, este será el único que no volvamos a guardar en el txt
    fun borrarJugador(jugador: Jugador) {
        var cadena = obtenerTextoArchivo()
        cadena = cadena.substring(0, cadena.length-1)
        var todos  = ""
        if (cadena.isNotEmpty()) {
            val jugadores = cadena.split(';')
            for (guardado in jugadores) {
                val datos = guardado.split(',')
                val nombre = datos[0]
                if (nombre.toUpperCase(Locale.ROOT) != jugador.nombre.toUpperCase(Locale.ROOT))
                    todos += "$guardado;"
            }
        }
        // tenemos que borrar el contenido del archivo antes de volver a guardar los jugadores
        vaciarArchivo()
        guardarTodos(todos)
    }

    // abrimos el archivo txt y lo vacíamos (pisamos el contenido anterior con una cadena vacía)
    private fun vaciarArchivo() {
        try {
            if (contextWrapper.fileList().contains(Constantes.ARCHIVO)){
                val archivo = OutputStreamWriter(contextWrapper.openFileOutput(Constantes.ARCHIVO,
                    AppCompatActivity.MODE_PRIVATE))
                archivo.write("")
                archivo.flush()
                archivo.close()
            }
        } catch (e: IOException) {
            Log.e("ARCHIVO", "Errror al recuperar el archivo")
        }
    }

    // en caso de querer solo guardar un jugador lo que hacemos es recoger los jugadores antiguos y
    // sobre ellos es donde añadimos al nuevo jugador
    fun guardarJugador(jugador: Jugador) {
        var cadena = "${jugador.nombre},${jugador.nivel};"
        cadena += obtenerTextoArchivo()
        guardarTodos(cadena)
    }

    // guardar en el archivo el listado completo de jugadores
    private fun guardarTodos(jugadores : String) {
        try {
            val archivo = OutputStreamWriter(contextWrapper.openFileOutput(Constantes.ARCHIVO, AppCompatActivity.MODE_PRIVATE))
            archivo.write(jugadores)
            archivo.flush()
            archivo.close()
        } catch (e: IOException){
            Log.e("ARCHIVO", "HA HABIDO UN PROBLEMA EN LA GRABACIÓN DEL ARCHIVO")
        }
    }

    // obtenemos el contenido del archivo txt y lo devolvemos como cadena para que se pueda
    // transformar en el listado de jugadores
    private fun obtenerTextoArchivo(): String {
        val cadena = StringBuilder()
        try {
            if (contextWrapper.fileList().contains(Constantes.ARCHIVO)){
                val archivo = InputStreamReader(contextWrapper.openFileInput((Constantes.ARCHIVO)))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                while(linea!=null){
                    cadena.append(linea)
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
            }
        } catch (e: IOException) {
            Log.e("ARCHIVO", "Errror al recuperar el archivo")
        }
        return cadena.toString()
    }

}