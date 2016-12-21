package hm.binkley.labs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

abstract class RestITBase {
    @Autowired lateinit private var restTemplate: TestRestTemplate

    protected fun get(path: String)
            = restTemplate.getForObject(path, String::class.java)
}
