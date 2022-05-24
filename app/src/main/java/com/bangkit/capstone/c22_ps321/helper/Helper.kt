package com.bangkit.capstone.c22_ps321.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

fun String.withDateFormat(): String {
    val dateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    val dateTimeFormat = dateTime.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(dateTimeFormat)
}

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())