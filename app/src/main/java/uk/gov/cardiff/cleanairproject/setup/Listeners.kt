package uk.gov.cardiff.cleanairproject.setup

/**
 * Contains the functions that will need to be accessible by fragments in SetupActivity.
 */
interface Listeners {
    fun changeFragmentListener(targetPage: Pages)
    fun login(email: String, password: String)
    fun register(email: String, password: String)
    fun requestLocationPermission()
    fun finishSetup()
}