package uk.gov.cardiff.cleanairproject.model

data class User (
    var id: Long = -1,
    var email: String,
    var password: String
)