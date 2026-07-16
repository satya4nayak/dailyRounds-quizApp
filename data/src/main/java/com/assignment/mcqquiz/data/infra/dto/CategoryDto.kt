package com.assignment.mcqquiz.data.infra.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("questions_url") val questionUrl: String
)


