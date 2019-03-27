package uk.gov.cardiff.cleanairproject.model

data class Journey (
    var id: Long = -1,
    var RemoteId: Long,
    var Synced: Boolean
)