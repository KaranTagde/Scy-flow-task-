package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedGitHubItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedGitHubItemDao {

    @Query("SELECT * FROM cached_github_items WHERE repo = :repo ORDER BY number DESC")
    fun getItemsForRepo(repo: String): Flow<List<CachedGitHubItemEntity>>

    @Query("SELECT * FROM cached_github_items WHERE repo = :repo ORDER BY number DESC")
    suspend fun getItemsForRepoDirect(repo: String): List<CachedGitHubItemEntity>

    @Query("SELECT * FROM cached_github_items ORDER BY cachedAtTimestamp DESC")
    fun getAllCachedItems(): Flow<List<CachedGitHubItemEntity>>

    @Query("SELECT COUNT(*) FROM cached_github_items")
    fun getCachedItemCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CachedGitHubItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CachedGitHubItemEntity)

    @Query("DELETE FROM cached_github_items WHERE repo = :repo")
    suspend fun clearRepo(repo: String)

    @Query("DELETE FROM cached_github_items")
    suspend fun clearAll()
}
