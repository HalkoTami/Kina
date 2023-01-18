package com.koronnu.kina.util

import java.text.SimpleDateFormat
import java.util.*

class DateTimeActions {
    enum class TimeUnit{
        SECONDS,
        MINUTES,
        HOURS,
        DAYS
    }
    private val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN)


    fun parentTimeToString():String{
        return formatter.format(Date()).toString()
    }
    fun fromStringToDate(string: String): Date?{
        return formatter.parse(string)
    }
    fun getTimeDifference(date1: Date,date2:Date,timeUnit: TimeUnit):Int{

        val diff: Long = date2.time - date1.time
        val numOfDays = (diff / (1000 * 60 * 60 * 24)).toInt()
        val hours = (diff / (1000 * 60 * 60)).toInt()
        val minutes = (diff / (1000 * 60)).toInt()
        val seconds = (diff / 1000).toInt()
        return when(timeUnit){
            TimeUnit.SECONDS -> seconds
            TimeUnit.MINUTES -> minutes
            TimeUnit.HOURS -> hours
            TimeUnit.DAYS -> numOfDays
        }
    }
}