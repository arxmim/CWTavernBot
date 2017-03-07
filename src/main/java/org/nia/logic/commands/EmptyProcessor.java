package org.nia.logic.commands;

import org.telegram.telegrambots.api.objects.Message;

/**
 * @author IANazarov
 */
public class EmptyProcessor implements CommandProcessor {
    @Override
    public String apply(Message message) {
        return "";
    }
}
