package uk.gov.cardiff.cleanairproject.auth

interface ServerAuthenticatorListener {
    fun onAuthSuccess(token: String)
    fun onAuthFailure(error: String)
}