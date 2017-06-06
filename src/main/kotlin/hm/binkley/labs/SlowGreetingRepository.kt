package hm.binkley.labs

import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.SECONDS

class SlowGreetingRepository : GreetingRepository {
    private val cache = ConcurrentHashMap<String, Future<String?>>()

    override fun create(name: String) {
        cache.putIfAbsent(name, supplyAsync {
            SECONDS.sleep(30)
            "Hello, $name!"
        })
    }

    override fun get(name: String): Progress {
        val job = cache[name] ?: throw IndexOutOfBoundsException(name)
        return if (job.isDone) Progress(job.get()!!, 100)
        else Progress(null, 0)
    }

    override fun delete(name: String) {
        cache.remove(name)?.cancel(true)
    }
}
