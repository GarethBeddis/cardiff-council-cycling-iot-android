package uk.gov.cardiff.cleanairproject.setup

interface Listeners {
    fun changeFragmentListener(targetPage: Pages)
    fun requestLocationPermission()
    fun finishSetup()
}