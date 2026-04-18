package com.example.onlineexaminationsystem.ui.sync

sealed class SyncState {
    object Idle:SyncState()
    object Queued:SyncState()
    object Syncing:SyncState()
    object Success:SyncState()
    object Failed:SyncState()
}