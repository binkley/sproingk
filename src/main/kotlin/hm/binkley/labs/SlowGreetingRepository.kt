package hm.binkley.labs

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit.SECONDS

class SlowGreetingRepository : GreetingRepository {
    private val cache
            = ConcurrentHashMap<String, CompletableFuture<Greeting>>()

    override fun find(name: String) {
        cache[name] = supplyAsync {
            SECONDS.sleep(1)
            Greeting(name)
        }
    }

    override fun ready(name: String) = cache(name).isDone

    override fun get(name: String) = cache(name).get()!!

    private fun cache(name: String)
            = cache[name] ?: throw IndexOutOfBoundsException(name)
}