package uk.gov.cardiff.cleanairproject.model;

import android.icu.lang.UCharacter
import android.provider.ContactsContract
import org.w3c.dom.Text
import java.sql.Time
import java.sql.Timestamp

data class Readings (

    val id: Int = -1,
    val RemoteId: Int,
    val JourneyId: Int,
    val JourneyRemoteId: Int,
    val NoiseReading: Double,
    val No2Reading: Double,
    val PM10Reading: Double,
    val PM25Reading: Double,
    val TimeTaken: Double,
    val Longitude: Int,
    val Latitude: Int,
    val Synced: Int

)
