package com.danilobarreto.stockapp.orders.data

import com.danilobarreto.stockapp.orders.data.dto.CreateOrderRequestDto
import com.danilobarreto.stockapp.orders.data.dto.ErrorResponseDto
import com.danilobarreto.stockapp.orders.data.dto.OrderDto
import com.danilobarreto.stockapp.orders.data.dto.UpdateOrderRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OrdersApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getOrders(ticker: String? = null): List<OrderDto> =
        httpClient.get("$baseUrl/orders") {
            ticker?.let { parameter("ticker", it) }
        }.body()

    suspend fun createOrder(dto: CreateOrderRequestDto): OrderDto =
        httpClient.post("$baseUrl/orders") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    suspend fun updateOrder(id: String, dto: UpdateOrderRequestDto): OrderDto =
        httpClient.patch("$baseUrl/orders/$id") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    suspend fun deleteOrder(id: String): OrderDto =
        httpClient.delete("$baseUrl/orders/$id").body()
}

suspend fun parseOrderErrorMessage(exception: ClientRequestException): String =
    runCatching { exception.response.body<ErrorResponseDto>().message }
        .getOrDefault("Não foi possível completar a operação")