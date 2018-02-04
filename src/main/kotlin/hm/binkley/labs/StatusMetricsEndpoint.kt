package hm.binkley.labs

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation

@Endpoint(id = "statusCounts")
class StatusMetricsEndpoint(private val service: StatusMetricsService) {
    @ReadOperation
    fun statusCounts() = service.statusCounts
}
