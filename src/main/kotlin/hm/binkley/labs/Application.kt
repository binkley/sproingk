package hm.binkley.labs

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
open class Application {
    @RequestMapping("/")
    fun home(): String {
        LoggerFactory.getLogger(Application::class.java).info("Hello, world!")
        return "Hello, world!"
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
