package uk.gov.cardiff.cleanairproject.model;

import android.provider.ContactsContract
import java.sql.Time

data class Readings (

    val id: Int = -1,
    val RemoteId: Int,
    val JourneyId: Int,
    val JourneyRemoteId: Int,
    val NoiseReading: Float,
    val No2Reading: Float,
    val PM10Reading: Float,
    val PM25Reading: Float,
    val TimeTaken: Time,
    val Longitude: Float,
    val Latitude: Float,
    val Synced: Boolean

)
