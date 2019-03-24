package uk.gov.cardiff.cleanairproject.foreground

interface ServiceCallback {
    fun onConnected()
    fun onReading(longitude: Double?, latitude: Double?, no2: Int, pm25: Int, pm100: Int, db: Int)
    fun onServiceStarted()
    fun onServiceStopped()
}