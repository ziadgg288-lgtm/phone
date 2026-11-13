package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey
    val key: String = "catalog_sync",
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "SUCCESS", // "SUCCESS", "FAILED", "OFFLINE", "SYNCING"
    val itemsCount: Int = 0,
    val lastErrorMessage: String? = null
)
