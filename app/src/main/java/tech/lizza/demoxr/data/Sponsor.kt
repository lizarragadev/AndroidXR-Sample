package tech.lizza.demoxr.data

data class Sponsor(
    val id: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val description: String? = null,
    val website: String? = null,
    val logoUrl: String? = null
)

