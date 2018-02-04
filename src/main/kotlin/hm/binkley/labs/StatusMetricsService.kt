package hm.binkley.labs

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class StatusMetricsService {
    /** @todo Why didn't {@code withDefault { _ -&gt; 0 } do the trick? */
    val statusCounts: Map<Int, Int> = object : ConcurrentHashMap<Int, Int>() {
        override operator fun get(key: Int) = super.get(key) ?: 0
    }

    fun increaseCount(status: Int) {
        (statusCounts as MutableMap<Int, Int>)
                .merge(status, 1) { a, b -> a + b }
    }
}
