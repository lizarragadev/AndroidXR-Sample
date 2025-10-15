package tech.lizza.demoxr.data

data class Talk(
    val id: String,
    val title: String,
    val description: String,
    val day: Int,
    val startTime: String,
    val endTime: String,
    val room: String,
    val level: TalkLevel,
    val category: String,
    val speakerId: String,
    val resources: List<String> = emptyList()
)

enum class TalkLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}
