package hm.binkley.labs

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController

val logger = LoggerFactory.getLogger(Application::class.java)!!

@EnableHystrixDashboard
@SpringBootApplication
@RestController
open class Application {
    @RequestMapping("/", method = arrayOf(GET))
    fun home(): String {
        logger.info("Hello, world!")
        return "Hello, world!\n"
    }
}

fun main(args: Array<String>) {
    run(Application::class.java, *args)
}
