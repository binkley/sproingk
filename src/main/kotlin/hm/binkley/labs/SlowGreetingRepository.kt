package hm.binkley.labs

import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.SECONDS

class SlowGreetingRepository : GreetingRepository {
    private val cache
            = ConcurrentHashMap<String, Future<String?>>()

    override fun create(name: String) {
        cache[name] = supplyAsync {
            SECONDS.sleep(10)
            "Hello, $name!"
        }
    }

    override fun get(name: String): String? {
        val job = cache(name)
        return if (job.isDone) job.get()!! else null
    }

    override fun delete(name: String) {
        cache.remove(name)?.cancel(true)
    }

    private fun cache(name: String)
            = cache[name] ?: throw IndexOutOfBoundsException(name)
}
