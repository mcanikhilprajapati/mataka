package com.instantonlinematka.instantonlinematka.utility

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit





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

    fun ConvertTimeDiff(time :String) : String {

        val dateFormat = SimpleDateFormat("HH:mm")
        val currentDate: String = dateFormat.format(Date()).toString()

        val simpleDateFormat = SimpleDateFormat("HH:mm")

        val date1 = simpleDateFormat.parse(currentDate)
       val  date2 = simpleDateFormat.parse(time)

        val difference: Long = date2.getTime() - date1.getTime()

        val seconds: Long = difference/1000
        val s: Long = seconds % 60

        val m: Long = seconds / 60 % 60

        val h: Long = seconds / (60 * 60) % 24

        return String.format("%02d:%02d:%02d", h, m, s)
//        val finalRe =  ""+pad(TimeUnit.MILLISECONDS.toHours(difference)) +":"+ pad(TimeUnit.MILLISECONDS.toMinutes(difference) % 60)+":"+pad(TimeUnit.MILLISECONDS.toSeconds(difference) % 60)
//        return finalRe
    }

    fun pad(number : Long) :String{
          if(number < 10)
                return  ("0" + number)
           else return  ""+number
    }
}