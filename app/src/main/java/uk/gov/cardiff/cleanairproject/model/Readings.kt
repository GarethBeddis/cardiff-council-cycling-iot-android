package uk.gov.cardiff.cleanairproject.model;

data class Readings (
    var id: Long = -1,
    var RemoteId: Int,
    var JourneyId: Int,
    var NoiseReading: Double,
    var No2Reading: Double,
    var PM10Reading: Double,
    var PM25Reading: Double,
    var TimeTaken: Double,
    var Longitude: Int,
    var Latitude: Int,
    var Synced: Int
)
