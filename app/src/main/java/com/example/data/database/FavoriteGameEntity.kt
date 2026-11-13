package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey
    val gameId: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)
