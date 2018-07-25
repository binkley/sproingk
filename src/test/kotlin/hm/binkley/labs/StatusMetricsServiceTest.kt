package hm.binkley.labs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

internal class StatusMetricsServiceTest {
    @Test
    fun defaults() {
        val metrics = StatusMetricsService()

        assertThat(metrics.statusCounts[OK.value()]).isEqualTo(0)
    }

    @Test
    fun increments() {
        val metrics = StatusMetricsService()

        metrics.increaseCount(OK.value())

        assertThat(metrics.statusCounts[OK.value()]).isEqualTo(1)
    }
}
