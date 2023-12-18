package com.sookmyung.hanmundan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sense(
    @SerialName("syntacticArgument")
    val syntacticArgument: String,
    @SerialName("syntacticAnnotation")
    val syntacticAnnotation: String,
    @SerialName("cat")
    val cat: String,
    @SerialName("definition")
    val definition: String,
    @SerialName("link")
    val senseLink: String,
    @SerialName("sense_no")
    val senseNo:String,
    @SerialName("target_code")
    val targetCode: String,
    @SerialName("type")
    val type: String,
    @SerialName("pos")
    val pos: String
)
