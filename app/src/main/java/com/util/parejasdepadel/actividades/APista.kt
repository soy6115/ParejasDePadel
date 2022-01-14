package com.util.parejasdepadel.actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.util.parejasdepadel.Constantes
import com.util.parejasdepadel.Jugadores
import com.util.parejasdepadel.R
import com.util.parejasdepadel.databinding.ActivityPistaBinding

class APista : AppCompatActivity() {

    private lateinit var jugadores : Jugadores
    private lateinit var binding: ActivityPistaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPistaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // recogemos los jugadores introducidos en la pantalla main
        // OJO el "as Jugadores", sirve para castear y pasar del serializable a jugador
        val bundle = intent.extras
        jugadores = bundle?.getSerializable(Constantes.PARAM_JUGADORES) as Jugadores

        pintarPartido()
        activarBotones()
    }

    private fun activarBotones() {
        val botonRehacer = binding.btRehacer
        botonRehacer.setOnClickListener {
            // si no ha gustado el sorteo, se vuelve a hacer y actualizamos luego el resultado
            jugadores.mezclarJugadores()
            pintarPartido()
        }

        val botonNivel = binding.btNivel
        // sabiendo los jugadores del partido, determinamos si todos tienen nivel (para hacer un
        // sorte justo) y si no todos tienen nivel, se hace un sorteo al azar
        val swMostrarNivel = jugadores.isJugadoresConNivel()
        if (swMostrarNivel) {
            botonNivel.setOnClickListener {
                // volvemos a hacer el sorteo y actualizamos la pantalla
                jugadores.ordenPorNivel()
                pintarPartido()
            }
            // en caso de que haya jugadores por nivel, lo que hacemos es cambiar el botón de
            // rehacer para que el nombre sea más acorde a la realidad
            botonRehacer.setBackgroundResource(R.drawable.botonazar_custom)
        }
        // si los jugadores no tienen nivel, no les permitimos ver el boton de sortear por nivel
        else
            botonNivel.visibility = View.GONE
    }

    private fun pintarPartido() {
        // usando el orden de los jugadores, indicamos el nombre en las cajas para mostrar el
        // resultado del sorteo
        val cajaResultado1 = binding.tvJugador1
        cajaResultado1.text = jugadores.getNombreJugador(1)
        val cajaResultado2 = binding.tvJugador2
        cajaResultado2.text = jugadores.getNombreJugador(2)
        val cajaResultado3 = binding.tvJugador3
        cajaResultado3.text = jugadores.getNombreJugador(3)
        val cajaResultado4 = binding.tvJugador4
        cajaResultado4.text = jugadores.getNombreJugador(4)
    }

    // para que haga la animacion al volver al principal
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_izq, R.anim.slide_out_der)
    }

}