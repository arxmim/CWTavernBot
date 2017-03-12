package org.nia.logic;

import org.telegram.telegrambots.api.objects.Message;

/**
 * @author Иван, 12.03.2017.
 */
public interface Commands {

    public String apply(Message message);
    public boolean isApplicable(Message message);
}
