package com.danilobarreto.stockapp.orders

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
