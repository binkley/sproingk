package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.PENDING
import io.micrometer.core.annotation.Timed
import io.micrometer.core.annotation.TimedSet
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.accepted
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid

// TODO: kotlinc complains about repeated annos without source retention
@RestController
class MainController(private val cache: GreetingBackgroundService) {
    @TimedSet(
        value = [
            Timed("timings.greetings"),
            Timed("timings.greetings.begin")]
    )
    @RequestMapping("/greetings", method = [POST])
    @ApiResponses(
        value = [
            ApiResponse(
                code = 303,
                message = "Navigate to completed greeting"
            ),
            ApiResponse(
                code = 202,
                message = "Navigate to greeting progress"
            ),
            ApiResponse(code = 422, message = "Invalid request")]
    )
    fun beginGreeting(@Valid @RequestBody request: GreetingRequest):
            ResponseEntity<*> {
        val name = request.name
        cache.create(name)
        val progress = cache[name]
        return if (progress.complete) goToResult(name, progress)
        else accepted()
            .location(URI.create("/queue/$name"))
            .body(Status(name, PENDING, progress.percentage))
    }

    @TimedSet(
        value = [
            Timed("timings.greetings"),
            Timed("timings.greetings.queue")]
    )
    @RequestMapping("/queue/{name}", method = [GET])
    @ApiResponses(
        value = [
            ApiResponse(
                code = 303,
                message = "Navigate to completed greeting"
            ),
            ApiResponse(code = 200, message = "Continue greeting progress")]
    )
    fun queue(@PathVariable name: String): ResponseEntity<Status> {
        val progress = cache[name]
        return if (progress.complete) goToResult(name, progress)
        else ok().body(Status(name, PENDING, progress.percentage))
    }

    @TimedSet(
        value = [
            Timed("timings.greetings"),
            Timed("timings.greetings.complete")]
    )
    @RequestMapping("/greetings/{name}", method = [GET])
    fun greetings(@PathVariable name: String): ResponseEntity<Greeting> {
        val progress = cache[name]
        return if (null != progress.greeting) ok(
            Greeting(
                progress.greeting,
                Status(name, COMPLETE, progress.percentage)
            )
        )
        else notFound().build<Greeting>()
    }

    @TimedSet(
        value = [
            Timed("timings.greetings"),
            Timed("timings.greetings.delete")]
    )
    @RequestMapping("/queue/{name}", "/greetings/{name}", method = [DELETE])
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable name: String) = cache.delete(name)

    @ExceptionHandler(IndexOutOfBoundsException::class)
    @ResponseStatus(NOT_FOUND)
    fun missingPerson() = Unit

    companion object {
        private fun goToResult(name: String, progress: Progress) =
            status(SEE_OTHER)
                .location(URI.create("/greetings/$name"))
                .body(Status(name, COMPLETE, progress.percentage))
    }
}
