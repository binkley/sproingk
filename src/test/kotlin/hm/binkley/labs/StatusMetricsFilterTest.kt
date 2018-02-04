package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus.OK
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletResponse

class StatusMetricsFilterTest {
    @Test
    @Throws(IOException::class, ServletException::class)
    fun filter200() {
        val service = StatusMetricsService()
        val filter = StatusMetricsFilter(service)

        val request = mock(ServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        `when`(response.status).thenReturn(OK.value())
        val chain = mock(FilterChain::class.java)
        filter.doFilter(request, response, chain)

        assertEquals(1, service.statusCounts[OK.value()] as Int)
    }
}
