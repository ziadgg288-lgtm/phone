package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_games ORDER BY savedAtTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_games WHERE gameId = :gameId)")
    fun isFavorite(gameId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteGameEntity)

    @Query("DELETE FROM favorite_games WHERE gameId = :gameId")
    suspend fun removeFavorite(gameId: String)
}
