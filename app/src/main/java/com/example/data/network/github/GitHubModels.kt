package com.example.data.network.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUserDto(
    @Json(name = "login") val login: String = "",
    @Json(name = "avatar_url") val avatarUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubBranchDto(
    @Json(name = "ref") val ref: String = ""
)

@JsonClass(generateAdapter = true)
data class GitHubLabelDto(
    @Json(name = "name") val name: String = "",
    @Json(name = "color") val color: String = ""
)

@JsonClass(generateAdapter = true)
data class GitHubPullRequestDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "number") val number: Int = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "state") val state: String = "open",
    @Json(name = "html_url") val htmlUrl: String = "",
    @Json(name = "user") val user: GitHubUserDto? = null,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "draft") val draft: Boolean = false,
    @Json(name = "head") val head: GitHubBranchDto? = null,
    @Json(name = "base") val base: GitHubBranchDto? = null
)

@JsonClass(generateAdapter = true)
data class GitHubIssueDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "number") val number: Int = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "state") val state: String = "open",
    @Json(name = "html_url") val htmlUrl: String = "",
    @Json(name = "user") val user: GitHubUserDto? = null,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "labels") val labels: List<GitHubLabelDto>? = null,
    @Json(name = "pull_request") val pullRequest: Any? = null
)

enum class GitHubItemType {
    PULL_REQUEST,
    ISSUE
}

data class GitHubWorkItem(
    val id: String,
    val number: Int,
    val title: String,
    val type: GitHubItemType,
    val state: String,
    val author: String,
    val authorAvatar: String?,
    val branchInfo: String,
    val htmlUrl: String,
    val updatedAt: String,
    val isDraft: Boolean = false,
    val labels: List<String> = emptyList()
)
