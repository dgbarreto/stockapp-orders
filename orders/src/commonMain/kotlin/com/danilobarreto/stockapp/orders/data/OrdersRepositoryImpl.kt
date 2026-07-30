package com.danilobarreto.stockapp.orders.data

import com.danilobarreto.stockapp.orders.data.dto.CreateOrderRequestDto
import com.danilobarreto.stockapp.orders.data.dto.UpdateOrderRequestDto
import com.danilobarreto.stockapp.orders.data.dto.toDomain
import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.Order
import com.danilobarreto.stockapp.orders.domain.OrderSide
import com.danilobarreto.stockapp.orders.domain.OrdersRepository

class OrdersRepositoryImpl(
    private val apiClient: OrdersApiClient,
) : OrdersRepository {
    override suspend fun getOrders(ticker: String?): List<Order> =
        apiClient.getOrders(ticker).map { it.toDomain() }

    override suspend fun createOrder(
        ticker: String,
        assetType: AssetType,
        side: OrderSide,
        quantity: Int,
        price: Double,
        fees: Double,
        executedAt: String,
    ): Order = apiClient.createOrder(
        CreateOrderRequestDto(ticker, assetType.name, side.name, quantity, price, fees, executedAt)
    ).toDomain()

    override suspend fun updateOrder(
        id: String,
        quantity: Int?,
        price: Double?,
        fees: Double?,
        executedAt: String?,
    ): Order = apiClient.updateOrder(id, UpdateOrderRequestDto(quantity, price, fees, executedAt)).toDomain()

    override suspend fun deleteOrder(id: String): Order =
        apiClient.deleteOrder(id).toDomain()
}