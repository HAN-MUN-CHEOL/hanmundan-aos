package com.sookmyung.hanmundan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dictionary(
    @SerialName("title")
    val title: String,
    @SerialName("link")
    val link: String,
    @SerialName("description")
    val description: String,
    @SerialName("total")
    val total: Int,
    @SerialName("start")
    val start: Int,
    @SerialName("num")
    val num: Int,
    @SerialName("item")
    val item: List<Item>
)
