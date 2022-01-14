package com.util.parejasdepadel.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.util.parejasdepadel.*
import com.util.parejasdepadel.databinding.ActivityMainBinding

/*
Es la clase que se encarga de gestionar la relación entre lo visual y el código necesario para la
pantalla principal
*/
class MainActivity : AppCompatActivity() {

    private lateinit var jugadoresGuardados : Jugadores
    private lateinit var binding: ActivityMainBinding
    private lateinit var cajas : ArrayList<AutoCompleteTextView>

    // sobreescribo el metodo onStart, para que se vuelva a realizar la carga de los jugadores
    // (necesario en el caso de que el usuario vuelva de la pantalla de jugadores)
    override fun onStart() {
        super.onStart()
        cargarJugadores()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        activarBotones()
        cargarJugadores()
    }

    private fun inicializarVariables() {
        // recogemos las cuatro cajas para guardado de nombres
        val caja1 = binding.acNombre1
        val caja2 = binding.acNombre2
        val caja3 = binding.acNombre3
        val caja4 = binding.acNombre4
        cajas = arrayListOf(caja1, caja2, caja3, caja4)
    }

    private fun activarBotones() {
        val botonSortear = binding.btSortear
        botonSortear.setOnClickListener {
            sortear(cajas)
        }

        val botonVerJugadores = binding.btVerJugadores
        botonVerJugadores.setOnClickListener {
            irAJugadores()
        }
    }

    private fun cargarJugadores() {
        // recuperamos del archivo txt los jugadores que tenemos guardados
        jugadoresGuardados = ConexionTXT(baseContext).obtenerJugadoresGuardados()

        // guardamos el listado de nombres para poder usarlo en el adapter y que los precargue en la
        // caja de nombres
        val nombresGuardados = obtenerNombreDeJugadoresGuardados()
        val adaptador: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, nombresGuardados)

        cajas.forEach {
            it.setAdapter(adaptador)
        }
    }

    private fun sortear(cajas : ArrayList<AutoCompleteTextView>) {
        val jugadores = crearPartido(cajas)
        irAPista(jugadores)
    }

    // Recogemos los nombres de los jugadores de las cajas,
    // y en ese momento determinamos como se hace el sorte de jugadores
    private fun crearPartido(cajas : ArrayList<AutoCompleteTextView>) : Jugadores {
        val jugadoresPartido = arrayListOf<Jugador>()
        var contador = 1
        for (caja in cajas){
            val nombre = caja.text.toString()
            if (nombre.isNotEmpty()){
                // Comprobamos si el jugador ya existía
                val jugadorGuardado = getJugadorGuardado(nombre)
                if (jugadorGuardado!=null)
                    jugadoresPartido.add(jugadorGuardado)
                else
                    jugadoresPartido.add(Jugador(nombre))
            } else {
                // Si no han rellenado el nombre, se pone por defecto el que le corresponde
                // por orden numérico
                val jugador = Jugador("Jugador $contador")
                jugadoresPartido.add(jugador)
            }
            contador++
        }
        val jugadores = Jugadores(jugadoresPartido)
        // de primeras hacemos una mezcla aleatoria de jugadores
        jugadores.mezclarJugadores()
        // en caso de que los jugadores tuvisen todos nivel, hacemos un sorteo justo basandonos en
        // el nivel que tiene los jugadores para equilibrarlo
        if (jugadores.isJugadoresConNivel())
            jugadores.ordenPorNivel()
        return jugadores
    }

    // Devolvemos al jugador si ya lo tenemos guardado
    private fun getJugadorGuardado(nombre: String): Jugador? {
        for (jugador in jugadoresGuardados.jugadores)
            if (jugador.nombre == nombre)
                return jugador
        return null
    }

    private fun obtenerNombreDeJugadoresGuardados(): ArrayList<String> {
        val array = arrayListOf<String>()
        for (jugador in jugadoresGuardados.jugadores) {
            array.add(jugador.nombre)
        }
        return array
    }

    // Como los jugadores ya nos vienen mezclados, solo los cargamos para recogerlos después
    // en la siguiente pantalla
    private fun irAPista(jugadores: Jugadores){
        val intentPista = Intent(this, APista::class.java)
        intentPista.putExtra(Constantes.PARAM_JUGADORES, jugadores)
        startActivity(intentPista)
        overridePendingTransition(R.anim.slide_in_der, R.anim.slide_out_izq)
    }

    // Vamos a la pantalla de gestión de jugadores
    private fun irAJugadores() {
        val intentJugadores = Intent(this, AJugadores::class.java)
        startActivity(intentJugadores)
        overridePendingTransition(R.anim.slide_in_izq, R.anim.slide_out_der)
        // no ponemos finish porque es muy probable que el usuario vuelva hacia atrás y en ese caso
        // se cerraría la aplicación
    }

}