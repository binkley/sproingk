package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK

class StatusMetricsEndpointTest {
    @Test
    fun delegates() {
        val service = StatusMetricsService()
        val endpoint = StatusMetricsEndpoint(service)

        service.increaseCount(OK.value())

        assertEquals(1, endpoint.statusCounts()[OK.value()] as Int)
    }
}
