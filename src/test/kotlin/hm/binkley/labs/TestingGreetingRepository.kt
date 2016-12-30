package hm.binkley.labs

import hm.binkley.labs.TestingGreetingRepository.State.COMPLETE
import hm.binkley.labs.TestingGreetingRepository.State.NONE
import hm.binkley.labs.TestingGreetingRepository.State.PENDING

class TestingGreetingRepository : GreetingRepository {
    enum class State {
        NONE, PENDING, COMPLETE
    }

    var state = NONE

    override fun create(name: String) {
        if (NONE == state) state = PENDING
    }

    override fun ready(name: String) = COMPLETE == state
    override fun get(name: String) = Greeting(name)
}
