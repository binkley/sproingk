package hm.binkley.labs

import javax.validation.constraints.NotEmpty

data class GreetingRequest(@get:NotEmpty val name: String)
