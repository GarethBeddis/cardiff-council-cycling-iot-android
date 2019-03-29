package uk.gov.cardiff.cleanairproject.setup

/**
 * Contains the functions that will need to be accessible by fragments in SetupActivity.
 */
interface Listeners {
    fun changeFragmentListener(targetPage: Pages)
    fun login(email: String, token: String)
    fun register(email: String, token: String)
    fun requestLocationPermission()
    fun finishSetup()
}