package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

internal class StatusMetricsServiceTest {
    @Test
    fun defaults() {
        val metrics = StatusMetricsService()

        assertEquals(metrics.statusCounts[OK.value()] as Int, 0)
    }

    @Test
    fun increments() {
        val metrics = StatusMetricsService()

        metrics.increaseCount(OK.value())

        assertEquals(metrics.statusCounts[OK.value()] as Int, 1)
    }
}
