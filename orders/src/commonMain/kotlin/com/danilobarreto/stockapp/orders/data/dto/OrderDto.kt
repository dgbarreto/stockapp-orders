package com.danilobarreto.stockapp.orders.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val ticker: String,
    val assetType: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    val fees: Double,
    val executedAt: String,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CreateOrderRequestDto(
    val ticker: String,
    val assetType: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    val fees: Double,
    val executedAt: String,
)

@Serializable
data class UpdateOrderRequestDto(
    val quantity: Int? = null,
    val price: Double? = null,
    val fees: Double? = null,
    val executedAt: String? = null,
)

@Serializable
data class ErrorResponseDto(
    val statusCode: Int,
    val message: String,
    val error: String? = null,
)