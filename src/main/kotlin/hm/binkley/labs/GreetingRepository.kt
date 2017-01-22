package hm.binkley.labs

interface GreetingRepository {
    fun create(name: String)
    operator fun get(name: String): String?
    fun delete(name: String)
}
