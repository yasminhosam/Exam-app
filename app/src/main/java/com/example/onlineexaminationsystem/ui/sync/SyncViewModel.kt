package com.example.onlineexaminationsystem.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val workManager: WorkManager
) : ViewModel() {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState = _syncState.asStateFlow()
    private var hasSeenActiveSync = false

    init {
        observeSncWork()

    }

    private fun observeSncWork() {
        workManager.getWorkInfosForUniqueWorkFlow("sync_work")
            .onEach { workInfos ->
                val work = workInfos.firstOrNull()
                when (work?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        hasSeenActiveSync=true
                       _syncState.value = SyncState.Queued
                    }

                    WorkInfo.State.RUNNING -> {
                        hasSeenActiveSync=true
                        _syncState.value = SyncState.Syncing
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        if (hasSeenActiveSync) {
                            _syncState.value = SyncState.Success
                            hasSeenActiveSync = false // Reset for the next sync
                        }
                    }

                    WorkInfo.State.FAILED -> {
                        if (hasSeenActiveSync) {
                            _syncState.value = SyncState.Failed
                            hasSeenActiveSync = false // Reset for the next sync
                        }
                    }

                    else -> {}
                }
            }
            .launchIn(
                scope = viewModelScope
            )

    }

    fun consumeSyncState() {
        _syncState.value = SyncState.Idle
    }


}