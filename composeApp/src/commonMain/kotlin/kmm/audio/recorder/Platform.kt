package kmm.audio.recorder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform