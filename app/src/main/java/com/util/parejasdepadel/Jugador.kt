package com.util.parejasdepadel

import java.io.Serializable

// La clase Jugador solo contiene el nombre del jugador y su nivel. Si no le indicamos un nivel
// en la creación, asignamos automáticamente el nivel 99 para usarlo como control.
data class Jugador(var nombre: String, var nivel: Int = 99): Serializable {

    // como sabemos que por defecto el nivel es 99, cualquier otro valor indica que si hemos puesto
    // nosotros el nivel al jugador
    fun tieneNivel(): Boolean {
        if (nivel != 99)
            return true
        return false
    }

}