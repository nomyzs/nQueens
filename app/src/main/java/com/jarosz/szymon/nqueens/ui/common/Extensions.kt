package com.jarosz.szymon.nqueens.ui.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Int.toBoardSizeFormat(): String = "${this}x${this}"

fun Long.toDurationFormat(): String = "${this / 1000},${(this + 5) / 10 % 100}s"

fun Long.toDateFormat(): String {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    return date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
}
