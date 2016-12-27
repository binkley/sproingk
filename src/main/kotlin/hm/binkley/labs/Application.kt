package hm.binkley.labs

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.context.ApplicationListener

private val logger = getLogger(Application::class.java)!!

@EnableHystrixDashboard
@SpringBootApplication
open class Application
    : ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    override fun onApplicationEvent(
            event: EmbeddedServletContainerInitializedEvent) = logger.info(
            "Ready on port ${event.embeddedServletContainer.port}")
}

fun main(args: Array<String>) {
    run(Application::class.java, *args)
}
