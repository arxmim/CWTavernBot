package org.nia.logic.commands;

import org.apache.commons.lang.StringUtils;
import org.nia.model.Battle;
import org.nia.model.lists.BattleBalance;
import org.nia.model.lists.CastleType;
import org.nia.strings.Emoji;
import org.telegram.telegrambots.api.objects.Message;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author IANazarov
 */
public class BattleProcessor implements CommandProcessor {
    static Pattern getPointsPattern() {
        String flags = StringUtils.join(Emoji.TF_FLAGS(), "|");
        return Pattern.compile("(" + flags + ").*замок\\+(\\d+)");
    }

    static Pattern getCastlePattern() {
        String flags = StringUtils.join(Emoji.TF_FLAGS(), "|");
        String battleResult = Emoji.ATK + "|" + Emoji.DEF;
        String result = Emoji.CUP + "|" + Emoji.FLAG;
        String battleBalances = StringUtils.join(Arrays.stream(BattleBalance.values()).map(BattleBalance::getMark).collect(Collectors.toList()), "|");

        return Pattern.compile("(" + battleResult + ").*(" + flags + ").*(замка|форт).*"
                + "(" + battleBalances + ").*\r\n"
                + "(" + Emoji.MEDAL + "Лидеры атаки:.*)\r\n"
                + "(" + Emoji.MEDAL + "Лидеры защиты:.*)\r\n"
                + "(" + result + ")(Форт под контролем|У атакующих отобрали|Атакующие разграбили замок на) (\\d+|" + flags + ")");
    }

    @Override
    public String apply(Message message) {
        String text = message.getText();
        Matcher matcher = getCastlePattern().matcher(text);
        List<Battle> battles = new ArrayList<>();
        while (matcher.find()) {
            Battle battle = new Battle();
            battle.setBattleTime(new Date((long) message.getForwardDate() * 1000));
            battle.setBattleBalance(BattleBalance.byMark(matcher.group(4)));
            battle.setCastleType(CastleType.getByEmoji(matcher.group(2)));
            String res = matcher.group(9);
            CastleType fortOwner = CastleType.getByEmoji(res);
            if (fortOwner != null) {
                battle.setFortOwner(fortOwner);
            } else {
                battle.setGoldEarned(Integer.valueOf(res));
            }
            battles.add(battle);
        }
        matcher = getPointsPattern().matcher(text);
        while (matcher.find()) {
            CastleType castleType = CastleType.getByEmoji(matcher.group(1));
            Optional<Battle> battleOptional = battles.stream().filter(b -> b.getCastleType() == castleType).findFirst();
            if (battleOptional.isPresent()) {
                battleOptional.get().setPoints(Long.valueOf(matcher.group(2)));
            }
        }
        StringBuilder sb = new StringBuilder();
        battles.forEach(sb::append);
        return sb.toString();
    }

    String applyText(String text, Date date) {
        Matcher matcher = getCastlePattern().matcher(text);
        List<Battle> battles = new ArrayList<>();
        while (matcher.find()) {
            Battle battle = new Battle();
            battle.setBattleTime(date);
            battle.setBattleBalance(BattleBalance.byMark(matcher.group(4)));
            battle.setCastleType(CastleType.getByEmoji(matcher.group(2)));
            String res = matcher.group(9);
            CastleType fortOwner = CastleType.getByEmoji(res);
            if (fortOwner != null) {
                battle.setFortOwner(fortOwner);
            } else {
                battle.setGoldEarned(Integer.valueOf(res));
            }
            battles.add(battle);
        }
        matcher = getPointsPattern().matcher(text);
        while (matcher.find()) {
            CastleType castleType = CastleType.getByEmoji(matcher.group(1));
            Optional<Battle> battleOptional = battles.stream().filter(b -> b.getCastleType() == castleType).findFirst();
            if (battleOptional.isPresent()) {
                battleOptional.get().setPoints(Long.valueOf(matcher.group(2)));
            }
        }
        StringBuilder sb = new StringBuilder();
        battles.forEach(b-> sb.append(b).append("\n"));
        return sb.toString();

    }
}
