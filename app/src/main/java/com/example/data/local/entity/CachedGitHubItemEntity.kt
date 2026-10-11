package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.network.github.GitHubItemType
import com.example.data.network.github.GitHubWorkItem

@Entity(tableName = "cached_github_items")
data class CachedGitHubItemEntity(
    @PrimaryKey val id: String,
    val repo: String, // e.g. "google/accompanist"
    val number: Int,
    val title: String,
    val type: String, // "PULL_REQUEST" or "ISSUE"
    val state: String,
    val author: String,
    val authorAvatar: String?,
    val branchInfo: String,
    val htmlUrl: String,
    val updatedAt: String,
    val isDraft: Boolean,
    val labelsJoined: String, // Comma-delimited list of labels
    val cachedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): GitHubWorkItem {
        return GitHubWorkItem(
            id = id,
            number = number,
            title = title,
            type = if (type == "PULL_REQUEST") GitHubItemType.PULL_REQUEST else GitHubItemType.ISSUE,
            state = state,
            author = author,
            authorAvatar = authorAvatar,
            branchInfo = branchInfo,
            htmlUrl = htmlUrl,
            updatedAt = updatedAt,
            isDraft = isDraft,
            labels = if (labelsJoined.isBlank()) emptyList() else labelsJoined.split(",")
        )
    }

    companion object {
        fun fromDomain(repo: String, domain: GitHubWorkItem): CachedGitHubItemEntity {
            return CachedGitHubItemEntity(
                id = domain.id,
                repo = repo,
                number = domain.number,
                title = domain.title,
                type = domain.type.name,
                state = domain.state,
                author = domain.author,
                authorAvatar = domain.authorAvatar,
                branchInfo = domain.branchInfo,
                htmlUrl = domain.htmlUrl,
                updatedAt = domain.updatedAt,
                isDraft = domain.isDraft,
                labelsJoined = domain.labels.joinToString(","),
                cachedAtTimestamp = System.currentTimeMillis()
            )
        }
    }
}
