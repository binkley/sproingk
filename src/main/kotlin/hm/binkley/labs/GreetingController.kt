package hm.binkley.labs

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController

@RestController
open class GreetingController {
    @RequestMapping("/", method = arrayOf(GET))
    fun home() = Greeting("Hello, world!")

    @RequestMapping("/{name}", method = arrayOf(GET))
    fun home(@PathVariable name: String) = Greeting("Hello, $name!")
}
