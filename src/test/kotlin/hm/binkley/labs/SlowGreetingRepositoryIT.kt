package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@DisplayName("GIVEN a slow greeting repository")
internal class SlowGreetingRepositoryIT {
    @DisplayName("WHEN it is in progress")
    @Nested
    inner class InProgress {
        @DisplayName("THEN it shows progress")
        @Test
        fun shouldBePartDone() {
            val repository = SlowGreetingRepository(2000, MILLISECONDS)
            repository.create("Brian")
            SECONDS.sleep(1)
            val percentage = repository["Brian"].percentage
            repository.delete("Brian")
            assertTrue(percentage in 1 until 100,
                    "$percentage not between 0 and 100")
        }
    }
}
