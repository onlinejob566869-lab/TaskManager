package com.taskmanager.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun isToday(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    fun isOverdue(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        return timestamp < System.currentTimeMillis() && !isToday(timestamp)
    }

    fun isUpcoming(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        return timestamp > System.currentTimeMillis()
    }

    fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    }

    fun getMonthName(month: Int): String {
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return if (month in 0..11) months[month] else ""
    }
}
