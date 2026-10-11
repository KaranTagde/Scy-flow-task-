package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedJiraTicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedJiraTicketDao {

    @Query("SELECT * FROM cached_jira_tickets ORDER BY cachedAtTimestamp DESC")
    fun getAllTickets(): Flow<List<CachedJiraTicketEntity>>

    @Query("SELECT * FROM cached_jira_tickets ORDER BY cachedAtTimestamp DESC")
    suspend fun getAllTicketsDirect(): List<CachedJiraTicketEntity>

    @Query("SELECT * FROM cached_jira_tickets WHERE projectKey = :projectKey ORDER BY cachedAtTimestamp DESC")
    fun getTicketsByProject(projectKey: String): Flow<List<CachedJiraTicketEntity>>

    @Query("SELECT * FROM cached_jira_tickets WHERE id = :id LIMIT 1")
    suspend fun getTicketById(id: String): CachedJiraTicketEntity?

    @Query("SELECT COUNT(*) FROM cached_jira_tickets")
    fun getTicketCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<CachedJiraTicketEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: CachedJiraTicketEntity)

    @Query("DELETE FROM cached_jira_tickets WHERE id = :id")
    suspend fun deleteTicketById(id: String)

    @Query("DELETE FROM cached_jira_tickets")
    suspend fun clearAll()
}
