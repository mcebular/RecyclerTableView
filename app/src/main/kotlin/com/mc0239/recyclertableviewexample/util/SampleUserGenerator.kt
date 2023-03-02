package com.mc0239.recyclertableviewexample.util

import com.mc0239.recyclertableviewexample.rows.UserCheckable
import java.util.*

object SampleUserGenerator {
    private val names = arrayOf("Ellie", "Trevor", "Hilly", "Julie", "Ebba", "Darin", "Mary", "Shelly", "Angie", "Nelson")
    private val surnames = arrayOf("Sniders", "Danielson", "Romilly", "Durand", "Vipond", "Wakefield", "Wilson")

    @JvmStatic
    fun generateUser(): UserCheckable {
        val r = Random()
        val u = UserCheckable()
        u.id = r.nextInt(1000)
        u.name = names[r.nextInt(names.size)]
        u.surname = surnames[r.nextInt(surnames.size)]
        u.username = u.name.substring(3) + u.surname.substring(3)
        u.checked = r.nextInt(100) < 10
        return u
    }
}