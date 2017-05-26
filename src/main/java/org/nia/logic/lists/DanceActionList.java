package org.nia.logic.lists;

import lombok.Getter;

/**
 * @author IANazarov
 */
@Getter
public enum DanceActionList {
    FOO1_1("\"Движение 1_1\""),
    FOO1_2("\"Движение 1_2\""),
    FOO1_3("\"Движение 1_3\""),
    FOO1_4("\"Движение 1_4\""),
    FOO2_1("\"Движение 2_1\""),
    FOO2_2("\"Движение 2_2\""),
    FOO2_3("\"Движение 2_3\""),
    FOO2_4("\"Движение 2_4\"");

    private String actionName;

    DanceActionList(String actionName) {
        this.actionName = actionName;
    }

    public String doName() {
        return "сделал " + actionName;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
