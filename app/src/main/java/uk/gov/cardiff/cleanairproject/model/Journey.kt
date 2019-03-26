package uk.gov.cardiff.cleanairproject.model

import org.w3c.dom.Text
import java.sql.RowId
import java.sql.Time
import java.sql.Timestamp



data class Journey (
    val id: Int = -1,
    val RemoteId: Int,
    val StartTime: Double,
    val EndTime: Double,
    val Synced: Int
)