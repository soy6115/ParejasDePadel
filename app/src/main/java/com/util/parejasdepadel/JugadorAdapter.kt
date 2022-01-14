package com.util.parejasdepadel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.util.parejasdepadel.actividades.AJugadores
import com.util.parejasdepadel.databinding.ItemJugadorBinding

// Necesitamos crear esta clase para que haga la carga del recicler
class JugadorAdapter(private val activity: AJugadores, val jugadores: Jugadores): RecyclerView.Adapter<JugadorAdapter.JugadorHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorHolder {
        val layOutInflater = LayoutInflater.from(parent.context )
        return JugadorHolder(layOutInflater.inflate(R.layout.item_jugador, parent, false))
    }

    override fun onBindViewHolder(holder: JugadorHolder, position: Int) {
        val jugador = jugadores.getJugador(position)
        holder.render(jugador)

        // como vamos a necesitar elemenos del contexto de la actividad, necesitamos hacer que la
        // función esté en el activity
        holder.botonEliminar.setOnClickListener {
            activity.confirmacionBorrado(jugador)
        }
        holder.botonModificar.setOnClickListener {
            activity.modificarJugador(jugador)
        }

    }

    override fun getItemCount(): Int = jugadores.size()

    class JugadorHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = ItemJugadorBinding.bind(view)
        // lo definimos aquí para poder usarlo en el onBindViewHolder
        val botonEliminar = binding.btEliminar
        val botonModificar = binding.btModificar

        fun render(jugador: Jugador) {
            // aquí es donde hace la carga de cada jugador, en caso de que no tenga nivel asignado
            // lo dejamos con el símbolo -
            var mostrarNivel = jugador.nivel.toString()
            if (!jugador.tieneNivel())
                mostrarNivel = "-"
            binding.tvNombre.text = jugador.nombre
            binding.tvNivel.text = mostrarNivel
        }

    }

}