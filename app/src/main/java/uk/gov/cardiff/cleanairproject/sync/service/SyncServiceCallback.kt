package uk.gov.cardiff.cleanairproject.sync.service

interface SyncServiceCallback {
    fun onSyncStateChange(state: SyncStates)
    fun onSyncServiceStopped()
}