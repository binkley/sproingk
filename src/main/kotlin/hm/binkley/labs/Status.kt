package hm.binkley.labs

import javax.persistence.Embeddable

/** @todo Merge with Progress? */
@Embeddable
data class Status(val name: String, val state: State, val percentage: Int)
