package com.sookmyung.hanmundan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item (
    @SerialName("word")
    val word: String,
    @SerialName("sense")
    val sense: List<Sense>
)