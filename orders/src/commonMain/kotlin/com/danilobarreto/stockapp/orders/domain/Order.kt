package com.danilobarreto.stockapp.orders.domain

data class Order(
    val id: String,
    val ticker: String,
    val assetType: AssetType,
    val side: OrderSide,
    val quantity: Int,
    val price: Double,
    val fees: Double,
    val executedAt: String,
    val createdAt: String,
    val updatedAt: String,
)