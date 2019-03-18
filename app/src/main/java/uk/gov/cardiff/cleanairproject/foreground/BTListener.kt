package uk.gov.cardiff.cleanairproject.foreground

import android.content.Context

interface BTListener {
    fun sendCommand(input: String)
    fun connect(context: Context)
    fun disconnect()
}