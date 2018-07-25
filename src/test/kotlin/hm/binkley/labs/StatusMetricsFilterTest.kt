package hm.binkley.labs

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletResponse

internal class StatusMetricsFilterTest {
    @Test
    fun filter200() {
        val service = StatusMetricsService()
        val filter = StatusMetricsFilter(service)

        val request: ServletRequest = mockk();
        val response: HttpServletResponse = mockk();
        every { response.status } returns OK.value()
        val chain: FilterChain = mockk(relaxed = true)

        filter.doFilter(request, response, chain)

        assertThat(service.statusCounts[OK.value()]).isEqualTo(1)
    }
}
