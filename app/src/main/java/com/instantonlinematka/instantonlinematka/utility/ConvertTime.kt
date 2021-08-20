package com.instantonlinematka.instantonlinematka.utility

import java.text.SimpleDateFormat
import java.util.*

object ConvertTime {

    fun ConvertTimeToPM(time :String) : String {

        try {
            val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val dateObj: Date = sdf.parse(time)!!
            return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(dateObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null!!
    }
}