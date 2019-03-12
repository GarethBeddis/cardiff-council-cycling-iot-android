package uk.gov.cardiff.cleanairproject.setup

/**
 * Contains the functions that will need to be accessible by fragments in SetupActivity.
 */
interface Listeners {
    fun changeFragmentListener(targetPage: Pages)
    fun requestLocationPermission()
    fun finishSetup()
    fun hideKeyboard()
}