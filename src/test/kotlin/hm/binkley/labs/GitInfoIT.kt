package hm.binkley.labs

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.util.JsonPathExpectationsHelper

@DisplayName("GIVEN a running application on a random port")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal open class GitInfoIT {
    @Autowired lateinit private var restTemplate: TestRestTemplate

    @DisplayName("WHEN info endpoint is called")
    @Nested
    inner class GitInfo {
        @DisplayName("THEN it contains GIT details")
        @Test
        fun shouldTalkAboutGit() {
            // TODO: Is there something nicer?
            JsonPathExpectationsHelper(".git").exists(
                    restTemplate.getForObject("/info", String::class.java)!!)
        }
    }
}
