package uk.gov.cardiff.cleanairproject.sync

interface SyncListener {
    fun onSyncSuccess()
    fun onSyncFailure()
}