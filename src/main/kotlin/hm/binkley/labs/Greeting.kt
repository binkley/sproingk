package hm.binkley.labs

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Greeting(
        @Id @GeneratedValue
        val id: Int = 0,
        val content: String,
        @Embedded
        val status: Status)
