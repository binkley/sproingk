package hm.binkley.labs

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.context.ApplicationListener
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController

private val logger = getLogger(Application::class.java)!!

@EnableHystrixDashboard
@SpringBootApplication
@RestController
open class Application
    : ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    override fun onApplicationEvent(
            event: EmbeddedServletContainerInitializedEvent) = logger.info(
            "Ready on port ${event.embeddedServletContainer.port}")

    @RequestMapping("/", method = arrayOf(GET))
    fun home() = "Hello, world!\n"

    @RequestMapping("/{name}", method = arrayOf(GET))
    fun home(@PathVariable name: String) = "Hello, $name!\n"
}

fun main(args: Array<String>) {
    run(Application::class.java, *args)
}
