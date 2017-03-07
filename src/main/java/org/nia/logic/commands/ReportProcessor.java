package org.nia.logic.commands;

import org.apache.commons.lang3.StringUtils;
import org.nia.model.BattleStat;
import org.nia.model.lists.CastleType;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author IANazarov
 */
public class ReportProcessor implements CommandProcessor {

    @Override
    public String apply(Message message) {
        try (BufferedReader reader = new BufferedReader(new StringReader(message.getText()))) {
            BattleStat battleStat = new BattleStat();
            battleStat.setUserID(message.getFrom().getId());
            extractProfileAtkDefLvl(battleStat, reader.readLine());
            reader.readLine();
            String line = reader.readLine();
            if (line.contains("Опыт")) {
                extractExp(battleStat, line);
            } else if (line.contains("Золото")) {
                extractGold(battleStat, line);
            }
            line = reader.readLine();
            if (line != null) {
                if (line.contains("Золото")) {
                    extractGold(battleStat, line);
                } else {
                    extractEquip(battleStat, line);
                }
            }
            line = reader.readLine();
            if (line != null) {
                extractEquip(battleStat, line);
            }
            battleStat.setReportDate(new Date((long) message.getDate() * 1000));
            battleStat.setBattleDate(new Date((long) message.getForwardDate() * 1000));
            battleStat.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Спасибо, боец! Ты был в защите или в атаке?";
    }

    private void extractEquip(BattleStat battleStat, String line) {
        battleStat.setGetOrLostEquip(line);
    }

    private void extractGold(BattleStat battleStat, String line) {
        String expStr = StringUtils.substringBetween(line, "Золото: ", " монет");
        if (expStr != null) {
            Integer exp = Integer.valueOf(expStr);
            battleStat.setGold(exp);
        }
    }

    private void extractExp(BattleStat battleStat, String line) {
        String expStr = StringUtils.substringBetween(line, "Опыт: ", " ед");
        if (expStr != null) {
            Integer exp = Integer.valueOf(expStr);
            battleStat.setExp(exp);
        }
    }

    private void extractProfileAtkDefLvl(BattleStat battleStat, String line) {
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
        battleStat.setCastleType(castleType);
        String name = StringUtils.substringBetween(line, castleType.getEmoji().toString(), Emoji.ATK.toString());
        battleStat.setName(name.trim());

        Matcher matcher = Pattern.compile("[0-9]+[+]?[0-9]*").matcher(line);
        if (matcher.find()) {
            String atk = matcher.group().trim();
            String[] splitAtk = atk.split("\\+");
            battleStat.setAtk(Arrays.stream(splitAtk).mapToInt(Integer::valueOf).sum());
        }
        if (matcher.find()) {
            String def = matcher.group().trim();
            String[] splitDef = def.split("\\+");
            battleStat.setDef(Arrays.stream(splitDef).mapToInt(Integer::valueOf).sum());
        }
        matcher = Pattern.compile("\\(\\d+ ур.\\)").matcher(line);
        if (matcher.find()) {
            String grp = matcher.group();
            battleStat.setLvl(Integer.valueOf(StringUtils.substring(grp, 1, grp.indexOf(" "))));
        }
    }
}
