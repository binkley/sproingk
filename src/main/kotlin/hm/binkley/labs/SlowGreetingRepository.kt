package hm.binkley.labs

import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SlowGreetingRepository(
        private val delay: Int, private val timeUnit: TimeUnit)
    : GreetingRepository {
    private val greetings = ConcurrentHashMap<String, ProgressiveGreeting>()

    private inner class ProgressiveGreeting(private val name: String) {
        val greeting = supplyAsync {
            var i = 0
            while (i < delay) {
                progress.set(100 * i / delay)
                timeUnit.sleep(1)
                ++i
            }
            "Hello, $name!"
        }!!
        val progress = AtomicInteger()
    }

    override fun create(name: String) {
        greetings.putIfAbsent(name, ProgressiveGreeting((name)))
    }

    override fun get(name: String): Progress {
        val e = greetings[name] ?: throw IndexOutOfBoundsException(name)
        return if (e.greeting.isDone) Progress(e.greeting.get()!!, 100)
        else Progress(null, e.progress.get())
    }

    override fun delete(name: String) {
        greetings.remove(name)?.greeting?.cancel(true)
    }
}
