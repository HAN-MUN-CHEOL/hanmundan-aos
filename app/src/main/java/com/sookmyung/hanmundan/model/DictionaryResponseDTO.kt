package com.sookmyung.hanmundan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DictionaryResponseDTO(
    @SerialName("channel")
    val channel: Dictionary
)
