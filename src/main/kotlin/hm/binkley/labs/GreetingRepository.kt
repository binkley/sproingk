package hm.binkley.labs

interface GreetingRepository {
    fun create(name: String)

    fun ready(name: String): Boolean

    operator fun get(name: String): Greeting
}
