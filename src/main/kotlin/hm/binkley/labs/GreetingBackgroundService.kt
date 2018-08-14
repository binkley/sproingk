package hm.binkley.labs

interface GreetingBackgroundService {
    fun create(name: String)
    operator fun get(name: String): Progress
    fun delete(name: String)
}
