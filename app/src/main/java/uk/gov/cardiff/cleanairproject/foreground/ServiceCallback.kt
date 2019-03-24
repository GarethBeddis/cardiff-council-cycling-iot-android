package uk.gov.cardiff.cleanairproject.foreground

interface ServiceCallback {
    fun onReading(longitude: Double?)
    fun onServiceStarted()
    fun onServiceStopped()
}