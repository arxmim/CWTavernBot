package org.nia.logic.commands;

import org.telegram.telegrambots.api.objects.Message;

/**
 * @author IANazarov
 */
public interface CommandProcessor {

    String apply(Message message);
}
