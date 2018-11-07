package ru.nia.tavern.model.types.facts

/**
 * @author Иван, 06.04.2017.
 */
interface Fact {
    val name: String
    val desc: String

    fun name(): String
}
