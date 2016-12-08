package hm.binkley.labs

import org.slf4j.LoggerFactory
import org.springframework.boot.Banner.Mode.OFF
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController

val logger = LoggerFactory.getLogger(Application::class.java)!!

@SpringBootApplication
@RestController
open class Application {
    @RequestMapping("/", method = arrayOf(GET))
    fun home(): String {
        logger.warn("Hello, world!")
        return "Hello, world!\n"
    }
}

fun main(args: Array<String>) {
    val app = SpringApplication(Application::class.java)
    app.setBannerMode(OFF)
    app.run(*args)
}
