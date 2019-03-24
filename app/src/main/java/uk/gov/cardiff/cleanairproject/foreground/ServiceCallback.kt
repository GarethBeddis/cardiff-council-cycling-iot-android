package uk.gov.cardiff.cleanairproject.foreground

interface ServiceCallback {
    fun onConnected()
    fun onReading(longitude: Double?)
    fun onServiceStarted()
    fun onServiceStopped()
}