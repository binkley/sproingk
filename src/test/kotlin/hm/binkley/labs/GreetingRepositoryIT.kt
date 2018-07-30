package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityManager

@DataJpaTest
@ExtendWith(SpringExtension::class)
internal class GreetingRepositoryIT(
        @Autowired val repository: GreetingRepository,
        @Autowired val entityManager: EntityManager) {
    @DisplayName("WHEN saving a greeting properly annotated")
    @Nested
    inner class Roundtrip {
        @Test
        fun `THEN is can be read back`() {
            val greeting = Greeting("Hello, world!",
                    Status("world", COMPLETE, 100))

            repository.saveAndFlush(greeting.copy())
            entityManager.clear()

            val matcher = ExampleMatcher.matching()
                    .withIgnoreNullValues()
                    .withIgnorePaths("id")
            val example = Example.of(greeting, matcher)

            assertThat(repository.findOne(example).get()).isEqualTo(greeting)
        }
    }
}
