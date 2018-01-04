package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.PENDING
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class GreetingController(private val repository: GreetingRepository) {
    @RequestMapping("/greetings", method = [POST])
    fun beginGreeting(
            @RequestBody greeting: BeginGreeting): ResponseEntity<*> {
        val name = greeting.name
        repository.create(name)
        val progress = repository[name]
        return if (progress.complete)
            status(SEE_OTHER).location(URI.create("/greetings/$name")).body(
                            Status(name, COMPLETE, progress.percentage))
        else status(ACCEPTED).location(URI.create("/queue/$name")).body(
                        Status(name, PENDING, progress.percentage))
    }

    @RequestMapping("/queue/{name}", method = [GET])
    fun queue(@PathVariable name: String) = try {
        val progress = repository[name]
        if (progress.complete)
            status(SEE_OTHER).location(URI.create("/greetings/$name")).body(
                            Status(name, COMPLETE, progress.percentage))
        else status(OK).body(Status(name, PENDING, progress.percentage))
    } catch (_: IndexOutOfBoundsException) {
        status(NOT_FOUND).build<Status>()
    }!!

    @RequestMapping("/greetings/{name}", method = [GET])
    fun greetings(@PathVariable name: String) = try {
        val progress = repository[name]
        if (null != progress.greeting)
            ok(Greeting(progress.greeting,
                    Status(name, COMPLETE, progress.percentage)))
        else
            status(NOT_FOUND).build()
    } catch (_: IndexOutOfBoundsException) {
        status(NOT_FOUND).build<Status>()
    }!!

    @RequestMapping("/queue/{name}", "/greetings/{name}", method = [DELETE])
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable name: String) = repository.delete(name)
}
