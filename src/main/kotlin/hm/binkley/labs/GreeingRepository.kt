package hm.binkley.labs

import org.springframework.data.jpa.repository.JpaRepository

interface GreeingRepository : JpaRepository<Greeting, Int>
