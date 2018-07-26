package hm.binkley.labs

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
internal class TestingConfiguration {
    @Bean
    @Primary
    open fun greetingService() = TestingGreetingService()
}
