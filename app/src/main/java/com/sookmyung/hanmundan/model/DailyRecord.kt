package com.sookmyung.hanmundan.model

data class DailyRecord(
    val word: String,
    val sentence: String,
    val date: String,
    var bookmark: Boolean
)