package hm.binkley.labs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

internal class StatusMetricsEndpointTest {
    @Test
    fun delegates() {
        val service = StatusMetricsService()
        val endpoint = StatusMetricsEndpoint(service)

        service.increaseCount(OK.value())

        assertThat(endpoint.statusCounts()[OK.value()]).isEqualTo(1)
    }
}
