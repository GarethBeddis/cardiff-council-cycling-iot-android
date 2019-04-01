package uk.gov.cardiff.cleanairproject.data.model;

data class Reading (
    var id: Long = -1,
    var JourneyId: Long,
    var JourneyRemoteId: Long?,
    var NoiseReading: Double,
    var No2Reading: Double,
    var PM10Reading: Double,
    var PM25Reading: Double,
    var TimeTaken: Long,
    var Longitude: Double,
    var Latitude: Double
)
