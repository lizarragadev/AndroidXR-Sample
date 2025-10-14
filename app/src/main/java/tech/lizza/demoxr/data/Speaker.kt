package tech.lizza.demoxr.data

data class Speaker(
    val id: String,
    val name: String,
    val title: String,
    val company: String,
    val biography: String,
    val imageUrl: String,
    val socialMedia: SocialMedia
)

data class SocialMedia(
    val twitter: String? = null,
    val linkedin: String? = null,
    val github: String? = null,
    val website: String? = null
)
