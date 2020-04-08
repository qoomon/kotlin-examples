package me.qoomon.examples

import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream

fun InputStream.readString(): String = bufferedReader().use(BufferedReader::readText)
fun OutputStream.writeString(string: String): Unit = write(string.toByteArray())
