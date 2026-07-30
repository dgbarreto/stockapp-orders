package com.danilobarreto.stockapp.orders.domain

interface OrdersRepository {
    suspend fun getOrders(ticker: String? = null): List<Order>

    suspend fun createOrder(
        ticker: String,
        assetType: AssetType,
        side: OrderSide,
        quantity: Int,
        price: Double,
        fees: Double,
        executedAt: String,
    ): Order

    suspend fun updateOrder(
        id: String,
        quantity: Int? = null,
        price: Double? = null,
        fees: Double? = null,
        executedAt: String? = null,
    ): Order

    suspend fun deleteOrder(id: String): Order
}