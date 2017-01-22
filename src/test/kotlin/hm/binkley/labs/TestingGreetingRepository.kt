package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.PENDING

class TestingGreetingRepository : GreetingRepository {
    var state: State? = null

    override fun create(name: String) {
        if (null == state) state = PENDING
    }

    override fun get(name: String) = when (state) {
        COMPLETE -> name
        PENDING -> null
        else -> throw IndexOutOfBoundsException(name)
    }

    override fun delete(name: String) {
        state = null
    }
}
