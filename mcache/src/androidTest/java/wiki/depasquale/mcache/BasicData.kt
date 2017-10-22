package wiki.depasquale.mcache

data class BasicData(
    val name: String,
    val innerData: InnerData
)

data class InnerData(
    val contents: String
)