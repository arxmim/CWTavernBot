package org.nia.logic.lists;

import lombok.Getter;

/**
 * @author IANazarov
 */
@Getter
public enum DanceActionList {
    VALS1("поклон"){
        @Override
        public String doName() {
            return "поклонился";
        }
    },
    VALS2("протянуть руку к партнеру") {
        @Override
        public String doName() {
            return "протянул руку к партнеру";
        }

        @Override
        public String willDoName() {
            return getActionName();
        }
    },
    VALS3("взмах рукой"),
    VALS4("обнять партнера за талию"){
        @Override
        public String doName() {
            return "обнял партнера за талию";
        }

        @Override
        public String willDoName() {
            return getActionName();
        }
    },
    VALS5("положить руку партнеру на плечо"){
        @Override
        public String doName() {
            return "положил руку партнеру на плечо";
        }

        @Override
        public String willDoName() {
            return getActionName();
        }
    },
    VALS6("начать кружиться"){
        @Override
        public String doName() {
            return "начал кружиться";
        }

        @Override
        public String willDoName() {
            return getActionName();
        }
    },
    VALS7("реверанс");

    private String actionName;

    DanceActionList(String actionName) {
        this.actionName = actionName;
    }

    public String doName() {
        return "сделал " + actionName;
    }

    public String willDoName() {
        return "сделать " + actionName;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
