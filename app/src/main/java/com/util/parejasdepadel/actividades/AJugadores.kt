package com.util.parejasdepadel.actividades

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.util.parejasdepadel.*
import com.util.parejasdepadel.databinding.ActivityJugadoresBinding

class AJugadores : AppCompatActivity() {

    private lateinit var binding: ActivityJugadoresBinding
    private lateinit var jugadoresBBDD : Jugadores
    private lateinit var alertNuevoJugador : AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJugadoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jugadoresBBDD = ConexionTXT(baseContext).obtenerJugadoresGuardados()
        cargarRecycler()

        val botonCrearNuevoJugador = binding.btNuevoJugador
        botonCrearNuevoJugador.setOnClickListener {
            // OJO, el método show devuelve un objeto de tipo AlertDialog , que lo necesitamos
            // guardar porque este sí tiene el método  dismiss para cerrar la ventana.
            // Si lo dejaramos con el AlertDialog.Builder no tengo como cerrarlo
            alertNuevoJugador = mostrarCrearNuevoJugador().show()
        }
    }

    // Hacemos la carga del recicler con los jugadores que hemos recogido
    private fun cargarRecycler() {
        binding.rvJugadores.layoutManager = LinearLayoutManager(this)
        val adapter = JugadorAdapter(this, jugadoresBBDD)
        binding.rvJugadores.adapter = adapter
        controlPantalla()
    }

    // mostramos una parte de la pantalla u otra según el número de jugadores
    private fun controlPantalla() {
        if (jugadoresBBDD.size() > 0) {
            binding.tvSinJugadores.visibility = View.GONE
            binding.divDatosJugadores.visibility = View.VISIBLE
        } else {
            binding.tvSinJugadores.visibility = View.VISIBLE
            binding.divDatosJugadores.visibility = View.GONE
        }
    }

    // es la creación del alert, se llama desde el Adapter
    private fun mostrarCrearNuevoJugador(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this, R.style.CajaCrearJugador)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.nuevo_jugador, null)
        builder.setView(view)

        val botonCrear = view.findViewById<Button>(R.id.btCrearJugador)
        val cajaNombre = view.findViewById<EditText>(R.id.etNombreNuevoJugador)
        val cajaNivel = view.findViewById<EditText>(R.id.etNivelNuevoJugador)
        lateinit var nuevoJugador : Jugador

        botonCrear.setOnClickListener {
            val nombre = cajaNombre.text.toString()
            var swCrear = false
            // comprobamos si se ha introducido algún nombre
            if (nombre.isNotEmpty()) {
                val nivel = cajaNivel.text.toString()
                // comprobamos si se ha indicado nivel
                if (nivel.isNotEmpty()) {
                    val nivelInt = nivel.toInt()
                    // no permitimos que haya nivel superior a 10 ni inferior a 1
                    if (nivelInt < 1 || nivelInt > 10)
                        Toast.makeText(this, "El nivel de jugador debe estar comprendido entre 1 y 10 incluidos", Toast.LENGTH_SHORT).show()
                    else {
                        swCrear = true
                        nuevoJugador = Jugador(nombre, nivelInt)
                    }
                }  else {
                    // permitimos la creación de jugadores sin nivel
                    swCrear = true
                    nuevoJugador = Jugador(nombre)
                }
                // en caso de que haya que crear al jugador, comprobamos si ya existe un jugador
                // con ese nombre, en caso de que no este creado lo guardamos
                if (swCrear) {
                    if (jugadoresBBDD.existeJugador(nombre)) {
                        Toast.makeText(this, "Ya existe un jugador con ese nombre, por favor prueba con otro", Toast.LENGTH_SHORT).show()
                    } else
                        guardarJugador(nuevoJugador)
                }
            } else {
                Toast.makeText(this, "Necesitamos un nombre para el jugador", Toast.LENGTH_SHORT).show()
            }
        }
        builder.create()
        return builder
    }

    private fun guardarJugador (jugador: Jugador) {
        // lo guardamos en el archivo txt
        ConexionTXT(baseContext).guardarJugador(jugador)
        // lo añadimos al array de jugadores guardados para no tener que ir a recogerlo de nuevo
        // al archivo txt
        jugadoresBBDD.addJugador(jugador)
        // cerramos el alert que habíamos abierto
        alertNuevoJugador.dismiss()
        // volvemos a cargar el recicler para que aparezca el nuevo jugador
        cargarRecycler()
    }

    // función para confirmar que estamos seguros de querer eliminar al jugaodor. Al tener sus
    // propios botones positive y negative no necesitamos cerrarlo aparte
    fun confirmacionBorrado(jugador: Jugador) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(this, R.style.CajaEliminar)
        builder.setTitle("Eliminar jugador")
        builder.setMessage("¿Seguro que quieres eliminar a ${jugador.nombre}?")
        builder.setPositiveButton("Sí") { _,_ ->
            eliminarJugador(jugador)
        }
        builder.setNegativeButton("No") { _,_ -> }
        // OJO!! Importante poner el .show() para que se muestre el alert de aviso
        builder.create().show()
        return builder
    }

    private fun eliminarJugador(jugador: Jugador) {
        // borramos al jugador del archivo txt
        ConexionTXT(baseContext).borrarJugador(jugador)
        // eliminamos al jugador del array de jugadores que ya teníamos instanciado
        jugadoresBBDD.eliminarJugador(jugador)
        // volvemos a cargar el recicler para eliminar al jugador de la lista
        cargarRecycler()
    }

    fun modificarJugador(jugador: Jugador) {
        // creamos el alert y lo mostramos directamente, se guarda también para poder cerrarlo luego
        alertNuevoJugador = mostrarModificarJugador(jugador).show()
    }

    private fun mostrarModificarJugador(jugador : Jugador): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this, R.style.CajaCrearJugador)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.nuevo_jugador, null)
        builder.setView(view)

        // como estamos usando el mismo botón que para crear el jugador, lo que tenemos que hacer es
        // cambiar el texto del literal
        val botonCrear = view.findViewById<Button>(R.id.btCrearJugador)
        botonCrear.text = resources.getString(R.string.btnGuardar)
        val cajaNombre = view.findViewById<EditText>(R.id.etNombreNuevoJugador)
        // dejamos escrito el nombre del jugador que ya había guardado
        cajaNombre.setText(jugador.nombre)
        val cajaNivel = view.findViewById<EditText>(R.id.etNivelNuevoJugador)
        // en caso de que el jugador tenga nivel, lo mostramos también en la pantalla
        val nivel = jugador.nivel
        if (jugador.tieneNivel())
            cajaNivel.setText(nivel.toString())

        lateinit var jugadorActualizado: Jugador

        botonCrear.setOnClickListener {
            // guardamos el nuevo nombre que están indicando
            val nombreActualizado = cajaNombre.text.toString()
            var swCrear = false
            // no permitimos que no haya nombre
            if (nombreActualizado.isEmpty()) {
                Toast.makeText(this, "Necesitamos un nombre para el jugador", Toast.LENGTH_SHORT).show()
            } else {
                // aquí comprobamos que no exista el nombre que queremos cambiar a uno ya existente
                // pero también hay que comprobar que no sea el mismo nombre que ya estaba usado
                // (por ejemplo en el caso de que solo fuera a actualizar el nivel)
                if (jugadoresBBDD.existeJugador(nombreActualizado) && jugador.nombre != nombreActualizado)
                    Toast.makeText(this, "Ya existe un jugador con ese nombre, por favor prueba con otro", Toast.LENGTH_SHORT).show()
                else {
                    // este bloque tiene las mismas comprobaciones que el guardar jugador
                    jugadorActualizado = Jugador(nombreActualizado)
                    val nivelNuevo = cajaNivel.text.toString()
                    if (nivelNuevo.isEmpty())
                        swCrear = true
                    else {
                        val nivelInt = nivelNuevo.toInt()
                        if (nivelInt < 1 || nivelInt > 10)
                            Toast.makeText(this, "El nivel de jugador debe estar comprendido entre 1 y 10 incluidos", Toast.LENGTH_SHORT).show()
                        else {
                            swCrear = true
                            jugadorActualizado.nivel = nivelInt
                        }
                    }
                    if (swCrear)
                        if (jugador == jugadorActualizado)
                            Toast.makeText(this, "No hay nada que modificar", Toast.LENGTH_SHORT).show()
                        else
                            actualizarJugador(jugador, jugadorActualizado)
                }
            }
        }
        builder.create()
        return builder
    }

    private fun actualizarJugador (jugadorViejo: Jugador, jugadorActualizado: Jugador) {
        val conexion = ConexionTXT(baseContext)
        // eliminamos el jugador antiguo del array y añadimos el actualizado
        jugadoresBBDD.eliminarJugador(jugadorViejo)
        jugadoresBBDD.addJugador(jugadorActualizado)
        // del archivo txt también eliminamos al viejo usuario y guardamos como si fuera nuevo
        conexion.borrarJugador(jugadorViejo)
        conexion.guardarJugador(jugadorActualizado)
        // cerramos la caja de datos
        alertNuevoJugador.dismiss()
        // volvemos a cargar el recicler para que actualice los datos
        cargarRecycler()
    }

    // para que haga la animacion al volver al principal
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_der, R.anim.slide_out_izq)
    }


}