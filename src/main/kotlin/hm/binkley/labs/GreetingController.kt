package hm.binkley.labs

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.HttpStatus.TEMPORARY_REDIRECT
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
open class GreetingController(private val repository: GreetingRepository) {
    @RequestMapping("/", method = arrayOf(GET))
    fun home() = "Hello, world!\n"

    @RequestMapping("/batch/{name}", method = arrayOf(GET))
    fun batch(@PathVariable name: String): ResponseEntity<*> {
        repository.create(name)
        return if (repository.ready(name))
            status(SEE_OTHER).
                    location(URI.create("/ready/$name")).
                    build()
        else status(TEMPORARY_REDIRECT).
                location(URI.create("/queue/$name")).
                build()
    }

    @RequestMapping("/queue/{name}", method = arrayOf(GET))
    fun queue(@PathVariable name: String) = try {
        if (repository.ready(name))
            status(SEE_OTHER).
                    location(URI.create("/ready/$name"))
        else status(OK)
    } catch (_: IndexOutOfBoundsException) {
        status(NOT_FOUND)
    }.build()!!

    @RequestMapping("/ready/{name}", method = arrayOf(GET))
    fun ready(@PathVariable name: String) = try {
        ok(repository[name])
    } catch (_: IndexOutOfBoundsException) {
        status(NOT_FOUND).build()
    }!!

    @RequestMapping("/queue/{name}", "/ready/{name}", method = arrayOf(DELETE))
    fun delete(@PathVariable name: String) {
        repository.delete(name)
    }
}
