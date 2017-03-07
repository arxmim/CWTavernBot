package org.nia.logic.commands;

import org.nia.model.Donator;
import org.nia.model.lists.Resource;
import org.telegram.telegrambots.api.objects.Message;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */
public class ForwardTradeProcessor implements CommandProcessor {

    @Override
    public String apply(Message message) {
        String res = "";
        try (BufferedReader reader = new BufferedReader(new StringReader(message.getText()))) {
            String name = reader.readLine().trim();
            Matcher matcher = Pattern.compile("([А-я ]+)([xXхХ]) (-?\\d+)").matcher(message.getText());
            while (matcher.find()) {
                String resourceName = matcher.group(1).trim();
                Integer count = Integer.valueOf(matcher.group(3));
                Resource resource = Resource.getByName(resourceName);
                if (resource != null) {
                    Donator donator = new Donator(name, resourceName, count);
                    donator.save();
                } else {
                    res += "Не найден ресурс: " + resourceName + "\n";
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Что-то пошло не так. Сообщение не обработано";
        }
        if (res.isEmpty()) {
            res = "Данные о торговой операции записаны";
        } else {
            res += "Остальные данные записаны";
        }
        return res;
    }
}
