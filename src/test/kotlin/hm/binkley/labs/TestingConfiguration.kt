package hm.binkley.labs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open internal class TestingConfiguration {
    @Bean
    open fun greetingRepository() = TestingGreetingRepository()
}
