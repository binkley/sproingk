package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.PENDING
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.accepted
import org.springframework.http.ResponseEntity.notFound
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
    fun beginGreeting(@RequestBody greeting: BeginGreeting): ResponseEntity<*> {
        val name = greeting.name
        repository.create(name)
        val progress = repository[name]
        return if (progress.complete)
            seeOther().location(URI.create("/greetings/$name")).body(
                    Status(name, COMPLETE, progress.percentage))
        else accepted().location(URI.create("/queue/$name")).body(
                Status(name, PENDING, progress.percentage))
    }

    @RequestMapping("/queue/{name}", method = [GET])
    fun queue(@PathVariable name: String) = try {
        val progress = repository[name]
        if (progress.complete)
            seeOther().location(URI.create("/greetings/$name")).body(
                    Status(name, COMPLETE, progress.percentage))
        else ok().body(Status(name, PENDING, progress.percentage))
    } catch (_: IndexOutOfBoundsException) {
        status(NOT_FOUND).build<Status>()
    }!!

    @RequestMapping("/greetings/{name}", method = [GET])
    fun greetings(@PathVariable name: String) = try {
        val progress = repository[name]
        if (null != progress.greeting)
            ok(Greeting(progress.greeting,
                    Status(name, COMPLETE, progress.percentage)))
        else notFound().build<Greeting>()
    } catch (_: IndexOutOfBoundsException) {
        notFound().build<Greeting>()
    }!!

    @RequestMapping("/queue/{name}", "/greetings/{name}", method = [DELETE])
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable name: String) = repository.delete(name)

    companion object {
        fun seeOther() = status(SEE_OTHER)
    }
}
