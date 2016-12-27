package hm.binkley.labs

interface GreetingRepository {
    fun find(name: String)

    fun ready(name: String): Boolean

    operator fun get(name: String): Greeting
}
