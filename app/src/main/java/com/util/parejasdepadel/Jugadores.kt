package com.util.parejasdepadel

import java.io.Serializable

// Creamos esta clase para agrupar a los jugadore
class Jugadores (var jugadores: ArrayList<Jugador>) : Serializable {

    // En el momento que alguno de los jugadores no tenga nivel, devolvemos false. Si todos tienen
    // nivel, devolvemos true
    fun isJugadoresConNivel (): Boolean {
        for (jugador in jugadores)
            if (!jugador.tieneNivel())
                return false
        return true
    }

    // para añadir un jugador individual dentro de los guardados
    fun addJugador(jugador: Jugador) {
        jugadores.add(jugador)
    }

    // para eliminar un jugador de los que ya estaban
    fun eliminarJugador (jugador: Jugador) {
        jugadores.remove(jugador)
    }

    // usado para hacer un sorteo aleatorio de los jugadores
    fun mezclarJugadores() {
        jugadores.shuffle()
    }

    // Cambiamos el orden de los jugadores según el nivel que tengan
    fun ordenPorNivel() {
        if (isJugadoresConNivel()) {
            val auxJugadores = arrayListOf<Jugador>()
            auxJugadores.addAll(jugadores.sortedByDescending {it.nivel})
            jugadores[0] = auxJugadores[0]
            jugadores[1] = auxJugadores[3]
            jugadores[2] = auxJugadores[1]
            jugadores[3] = auxJugadores[2]
        }
    }

    // dado el nombre de un jugador, comprobamos si ese nombre ya existía en los jugadores
    fun existeJugador (nombre : String) : Boolean {
        for (jugador in jugadores)
        /* Así es como lo tenía yo, pero me recomienda cambiarlo como el siguiente if
        //if (jugador.nombre.toUpperCase() == nombre.toUpperCase()) */
            if (jugador.nombre.equals(nombre, ignoreCase = true))
                return true
        return false
    }

    // se usa en el JugadorAdapter
    fun size(): Int {
        return jugadores.size
    }

    fun getJugador(position: Int): Jugador {
        return jugadores[position]
    }

    // devolvemos el nombre del jugador indicando que número de jugador es (ponemos el -1 porque los
    // números de jugadores que pasamos es del 1 al 4)
    fun getNombreJugador(numJugador:  Int): String {
        return jugadores[numJugador - 1].nombre
    }

}