package hm.binkley.labs

import org.springframework.data.jpa.repository.JpaRepository

interface GreetingRepository : JpaRepository<Greeting, Int>
