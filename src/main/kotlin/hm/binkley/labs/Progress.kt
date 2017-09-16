package hm.binkley.labs

/** @todo Merge with Status? */
data class Progress(val greeting: String?, val percentage: Int) {
    val complete
        get() = 100 == percentage

    companion object {
        fun start() = Progress(null, 0)
        fun complete(greeting: String?) = Progress(greeting, 100)
    }
}
