package com.danilobarreto.stockapp.orders.data.dto

import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.Order
import com.danilobarreto.stockapp.orders.domain.OrderSide

fun OrderDto.toDomain(): Order = Order(
    id = id,
    ticker = ticker,
    assetType = AssetType.valueOf(assetType),
    side = OrderSide.valueOf(side),
    quantity = quantity,
    price = price,
    fees = fees,
    executedAt = executedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)