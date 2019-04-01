package uk.gov.cardiff.cleanairproject.sensors

interface SensorServiceCallback {
    fun onConnected()
    fun onReading(longitude: Double?, latitude: Double?, no2: Int, pm25: Int, pm100: Int, db: Int)
    fun onSensorServiceStarted()
    fun onSensorServiceStopped()
}