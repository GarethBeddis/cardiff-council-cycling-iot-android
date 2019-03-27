package uk.gov.cardiff.cleanairproject.model

data class Journey (
    var id: Long = -1,
    var RemoteId: Int,
    var StartTime: Double,
    var EndTime: Double,
    var Synced: Int
)