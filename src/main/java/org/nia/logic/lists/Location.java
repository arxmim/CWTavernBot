package org.nia.logic.lists;

import org.nia.logic.quests.QuestsEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author IANazarov
 */
public enum Location {
    TAVERN(),
    QUEST();


    public static QuestsEnum getRandomQuest() {
        List<QuestsEnum> list = Arrays.stream(QuestsEnum.values()).collect(Collectors.toList());
        return list.get(new Random().nextInt(list.size()));
    }
}
