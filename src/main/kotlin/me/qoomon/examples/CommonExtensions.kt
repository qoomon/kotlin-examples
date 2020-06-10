package me.qoomon.examples

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stackTraceAsString(): String {
    val stringWriter = StringWriter()
    this.printStackTrace(PrintWriter(stringWriter))
    return stringWriter.toString()
}
