package hm.binkley.labs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@DisplayName("GIVEN a slow greeting repository")
internal class SlowGreetingBackgroundServiceIT {
    @DisplayName("WHEN it is in progress")
    @Nested
    inner class InProgress {
        @DisplayName("THEN it shows progress")
        @Test
        fun shouldBePartDone() {
            val repository = SlowGreetingBackgroundService(2000, MILLISECONDS)
            repository.create("Brian")
            SECONDS.sleep(1)
            val percentage = repository["Brian"].percentage
            repository.delete("Brian")

            assertThat(percentage in 1 until 100)
                    .`as`("$percentage not between 0 and 100")
                    .isTrue()
        }
    }

    @DisplayName("WHEN it is completed")
    @Nested
    inner class Completed {
        @DisplayName("THEN it shows 100% progress")
        @Test
        fun shouldBePartDone() {
            val repository = SlowGreetingBackgroundService(1, MILLISECONDS)
            repository.create("Brian")
            SECONDS.sleep(1)
            val percentage = repository["Brian"].percentage
            repository.delete("Brian")

            assertThat(percentage).isEqualTo(100)
        }
    }
}
