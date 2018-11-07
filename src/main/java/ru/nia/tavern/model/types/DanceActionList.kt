package ru.nia.tavern.model.types

import lombok.Getter

/**
 * @author IANazarov
 */
@Getter
enum class DanceActionList private constructor(val actionName: String) {
    FOO1_1("\"Движение 1_1\""),
    FOO1_2("\"Движение 1_2\""),
    FOO1_3("\"Движение 1_3\""),
    FOO1_4("\"Движение 1_4\""),
    FOO2_1("\"Движение 2_1\""),
    FOO2_2("\"Движение 2_2\""),
    FOO2_3("\"Движение 2_3\""),
    FOO2_4("\"Движение 2_4\"");

    fun doName(): String {
        return "сделал $actionName"
    }

    override fun toString(): String {
        return actionName
    }
}
