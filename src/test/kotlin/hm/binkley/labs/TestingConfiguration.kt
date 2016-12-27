package hm.binkley.labs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open internal class TestingConfiguration {
    @Bean
    @Primary
    open fun greetingRepository() = TestingGreetingRepository()
}
