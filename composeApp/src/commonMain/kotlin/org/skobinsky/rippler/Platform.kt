package org.skobinsky.rippler

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform