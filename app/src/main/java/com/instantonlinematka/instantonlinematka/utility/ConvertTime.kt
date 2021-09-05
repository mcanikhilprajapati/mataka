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


    fun getTimeDiff(time :String) : Long {

        val dateFormat = SimpleDateFormat("HH:mm")
        val currentDate: String = dateFormat.format(Date()).toString()

        val simpleDateFormat = SimpleDateFormat("HH:mm")

        val date1 = simpleDateFormat.parse(currentDate)
        val  date2 = simpleDateFormat.parse(time)

        val difference: Long = date2.getTime() - date1.getTime()

        val seconds: Long = difference/1000

        return difference
    }

    fun pad(number : Long) :String{
          if(number < 10)
                return  ("0" + number)
           else return  ""+number
    }

    fun getCountTimeByLong(finishTime: Long): String? {
        var totalTime = (finishTime / 1000).toInt() //秒
        var hour = 0
        var minute = 0
        var second = 0
        if (3600 <= totalTime) {
            hour = totalTime / 3600
            totalTime = totalTime - 3600 * hour
        }
        if (60 <= totalTime) {
            minute = totalTime / 60
            totalTime = totalTime - 60 * minute
        }
        if (0 <= totalTime) {
            second = totalTime
        }
        val sb = StringBuilder()
        if (hour < 10) {
            sb.append("0").append(hour).append(":")
        } else {
            sb.append(hour).append(":")
        }
        if (minute < 10) {
            sb.append("0").append(minute).append(":")
        } else {
            sb.append(minute).append(":")
        }
            sb.append("00")
        return sb.toString()
    }


}