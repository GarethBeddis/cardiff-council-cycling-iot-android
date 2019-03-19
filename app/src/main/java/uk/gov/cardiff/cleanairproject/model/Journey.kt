package uk.gov.cardiff.cleanairproject.model

import java.sql.RowId
import java.time.format.DateTimeFormatter

data class Journey (
    val id: Int = -1,
    val RemoteId: Int,
    val StartTime: DateTimeFormatter,
    val EndTime: DateTimeFormatter,
    val Synced: Boolean
)