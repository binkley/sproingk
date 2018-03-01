package hm.binkley.labs

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletResponse

@WebFilter("/*")
class StatusMetricsFilter(private val service: StatusMetricsService)
    : Filter {
    override fun init(filterConfig: FilterConfig) {}

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        chain.doFilter(request, response)
        service.increaseCount((response as HttpServletResponse).status)
    }

    override fun destroy() {}
}
