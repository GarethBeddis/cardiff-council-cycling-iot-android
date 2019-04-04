package uk.gov.cardiff.cleanairproject.sync.manager

interface SyncListener {
    fun onSyncSuccess()
    fun onSyncFailure()
}