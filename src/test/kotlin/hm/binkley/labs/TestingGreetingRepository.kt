package hm.binkley.labs

class TestingGreetingRepository : GreetingRepository {
    var done = false

    override fun find(name: String) = Unit
    override fun ready(name: String) = done
    override fun get(name: String)
            = if (done) Greeting(name) else throw IllegalStateException(name)
}
