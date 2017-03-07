package org.nia.logic.commands;

import org.apache.commons.lang3.StringUtils;
import org.nia.model.Equipment;
import org.nia.model.Profile;
import org.nia.model.lists.CastleType;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */
public class ForwardProcessor implements CommandProcessor {
    @Override
    public String apply(Message message) {

        try (BufferedReader reader = new BufferedReader(new StringReader(message.getText()))) {
            reader.readLine();
            reader.readLine();
            Profile profile = new Profile();
            extractCastleAndName(profile, reader.readLine());
            extractLevel(profile, reader.readLine());
            extractStat(profile, reader.readLine());
            extractExp(profile, reader.readLine());
            extractGold(profile, reader.readLine());
            extractCrystals(profile, reader.readLine());
            extractStamina(profile, reader.readLine());
            extractBag(profile, reader.readLine());
            extractEquipment(profile, reader);
            profile.setUserID(message.getFrom().getId());
            profile.save();
            if (!message.isUserMessage()) {
                profile.updateTeam(profile.getName(), message.getChat().getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Боец, данные твоего профиля обновлены.";
    }

    private void extractEquipment(Profile profile, BufferedReader reader) throws IOException {
        reader.readLine();
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null && !line.equals("Состояние:") && !line.isEmpty()) {
            String name = line.substring(0, line.indexOf("+"));
            Matcher matcher = Pattern.compile("[+][0-9]+" + Emoji.ATK.toString()).matcher(line);
            int atk = 0;
            if (matcher.find()) {
                String atkStr = matcher.group();
                atk = Integer.valueOf(atkStr.substring(1, atkStr.indexOf(Emoji.ATK.toString())));
            }
            int def = 0;
            matcher = Pattern.compile("[+][0-9]+" + Emoji.DEF.toString()).matcher(line);
            if (matcher.find()) {
                String defStr = matcher.group();
                def = Integer.valueOf(defStr.substring(1, defStr.indexOf(Emoji.DEF.toString())));
            }
            Equipment equipment = new Equipment(profile, name, atk, def);
            profile.addToEquipmentList(equipment);
        }
    }

    private void extractGold(Profile profile, String line) {
        String gold = StringUtils.substringAfter(line, "Золото: ");
        profile.setGold(Integer.valueOf(gold));
    }

    private void extractCrystals(Profile profile, String line) {
        String crystalls = StringUtils.substringAfter(line, "Кристаллы: ");
        profile.setCrystalls(Integer.valueOf(crystalls));
    }

    private void extractExp(Profile profile, String line) {
        String exp = StringUtils.substringBetween(line, "Опыт: ", " из ");
        profile.setExp(Integer.valueOf(exp));
    }

    private void extractStamina(Profile profile, String line) {
        String stamMax = StringUtils.substringAfter(line, "из ");
        profile.setStamina(Integer.valueOf(stamMax));
    }

    private void extractBag(Profile profile, String line) {
        String bag = StringUtils.substringBetween(line, "Рюкзак: ", "/");
        profile.setBag(Integer.valueOf(bag));
    }

    private void extractStat(Profile profile, String line) {
        Matcher matcher = Pattern.compile(" [0-9]+[+]?[0-9]*").matcher(line);
        if (matcher.find()) {
            String atk = matcher.group().trim();
            String[] splitAtk = atk.split("\\+");
            profile.setAtk(Arrays.stream(splitAtk).mapToInt(Integer::valueOf).sum());
        }
        if (matcher.find()) {
            String def = matcher.group().trim();
            String[] splitDef = def.split("\\+");
            profile.setDef(Arrays.stream(splitDef).mapToInt(Integer::valueOf).sum());
        }
    }

    private void extractLevel(Profile profile, String line) {
        String lvl = StringUtils.substringAfter(line, "Уровень: ");
        profile.setLvl(Integer.valueOf(lvl));
    }

    private void extractCastleAndName(Profile profile, String line) {
        CastleType castleType = null;
        for (CastleType type : CastleType.values()) {
            if (line.startsWith(type.getEmoji().toString())) {
                castleType = type;
                break;
            }
        }
        if (castleType == null) {
            throw new IllegalStateException("Can't parse line:\n" + line);
        }
        String name = line.substring(castleType.getEmoji().toString().length(), line.lastIndexOf(","));
        profile.setName(name);
        profile.setCastleType(castleType);
    }
}
