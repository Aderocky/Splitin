package com.example.splitin.data.model

import java.util.UUID

data class SplitHistory(
    val id: String = UUID.randomUUID().toString(),
    val activityName: String,
    val date: Long,
    val totalPeople: Int,
    val totalAmount: Long
)

data class Friend(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)

data class FriendSplitResult(
    val friend: Friend,
    val subtotal: Double,
    val taxShare: Double,
    val serviceShare: Double,
    val discountShare: Double,
    val finalAmount: Long,
    val isPayer: Boolean
)

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Long,
    val eaterIds: List<String>
)

