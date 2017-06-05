package hm.binkley.labs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

private val logger = getLogger(Application::class.java)!!

@EnableSwagger2
@SpringBootApplication
class Application
    : ApplicationListener<WebServerInitializedEvent> {
    override fun onApplicationEvent(
            event: WebServerInitializedEvent) = logger.info(
            "Ready on port ${event.webServer.port}")

    @Bean
    fun greetingRepository() = SlowGreetingRepository()

    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun api() = Docket(SWAGGER_2).
            select().
            apis(RequestHandlerSelectors.any()).
            paths(PathSelectors.any()).
            build()!!

    /**
     * @see <a href="https://springfox.github.io/springfox/docs/current/#q13">Q. How does one configure swagger-ui for non-springboot applications?</a>
     * @todo This is less than elegant, can it be done better?
     * */
    @Bean
    fun forwardToIndex() = object : WebMvcConfigurer {
        override fun addViewControllers(registry: ViewControllerRegistry?) {
            registry!!.addRedirectViewController("/", "/swagger-ui.html")
        }
    }
}

fun main(args: Array<String>) {
    run(Application::class.java, *args)
}
