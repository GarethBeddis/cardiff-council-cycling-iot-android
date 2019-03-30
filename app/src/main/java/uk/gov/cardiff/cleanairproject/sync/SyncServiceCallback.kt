package uk.gov.cardiff.cleanairproject.sync

interface SyncServiceCallback {
    fun onSyncStateChange(state: SyncStates)
    fun onSyncServiceStopped()
}