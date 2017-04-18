package hm.binkley.labs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean

private val logger = getLogger(Application::class.java)!!

@EnableHystrixDashboard
@SpringBootApplication
class Application
    : ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    override fun onApplicationEvent(
            event: EmbeddedServletContainerInitializedEvent) = logger.info(
            "Ready on port ${event.embeddedServletContainer.port}")

    @Bean
    fun greetingRepository() = SlowGreetingRepository()

    @Bean
    fun objectMapper() = jacksonObjectMapper()
}

fun main(args: Array<String>) {
    run(Application::class.java, *args)
}
