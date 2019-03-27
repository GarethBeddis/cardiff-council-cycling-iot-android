package uk.gov.cardiff.cleanairproject.model;

data class Reading (
    var id: Long = -1,
    var RemoteId: Long,
    var JourneyId: Long,
    var NoiseReading: Double,
    var No2Reading: Double,
    var PM10Reading: Double,
    var PM25Reading: Double,
    var TimeTaken: Long,
    var Longitude: Double,
    var Latitude: Double,
    var Synced: Boolean
)
