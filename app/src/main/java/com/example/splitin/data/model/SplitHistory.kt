package com.example.splitin.data.model

import java.util.UUID

data class SplitHistory(
    val id: String = UUID.randomUUID().toString(),
    val activityName: String,
    val date: Long,
    val totalPeople: Int,
    val totalAmount: Long
)
