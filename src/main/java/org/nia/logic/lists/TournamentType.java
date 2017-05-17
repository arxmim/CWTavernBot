package org.nia.logic.lists;

import org.nia.bots.CWTavernBot;
import org.nia.logic.commands.ArenaCommands;
import org.nia.logic.commands.Commands;
import org.nia.logic.commands.FightClubCommands;
import org.nia.logic.commands.PersonalCommands;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Иван, 11.03.2017.
 */
public enum TournamentType {
    FIGHT_CLUB("\"Бойцовский клуб\"") {
        @Override
        public List<? extends Commands> getCommands() {
            return Arrays.asList(FightClubCommands.values());
        }

        @Override
        protected void init() {
            setTie("Боец %s точным ударом пятки в глаз сбил соперника с ног. Падая, %s пнул обидчика чуть пониже спины и опрокинул его. " +
                    "Оба оказались настолько пьяны, что не смогли встать.\n\n ОБЪЯВЛЯЕТСЯ НИЧЬЯ! Бой состоится заново!");
            setRule("Жмите /DRAKA и пусть победит сильнейший!");
            addWinPhrase("%s быстро и решительно нокаутировал %s точным ударом. Чистая победа!");
            addWinPhrase("%s оказался #чутьсильнее %s. Возможно дело было в ножке от стула, но это не точно.");
            for (DrinkType dt : DrinkType.values()) {
                addWinPhrase("%s оказался #чутьсильнее %s. Возможно это из-за выпитого "+dt.getCommand() +" перед боем, но это не точно.");
                addStartPhrase("Прокричав, что " + dt.getCommand() + " уже не тот, %s метнул стакан в группу поддержки противника и обозначил свою готовность.");
                addStartPhrase("%s сказал всем, что готов к драке и нальёт всем " + dt.getCommand() + ", если победит.");
            }
            addWinPhrase("Лизонька крикнула, что любит %s и готова наливать ему каждый день! У %s разбилось сердце.");
            addWinPhrase("В последний момент бойцы передумали сражаться и решили определить победителя партией в шахматы. В итоге %s засунул ферзя бойцу %s в, гм, ухо и победил!");
            addWinPhrase("%s достал свою рапиру и заколол %s. Даже ПМС не помог!");
            addWinPhrase("Я твою мамку в кино водил! - заявил %s. Безоговорочная победа над бойцом %s!");
            addStartPhrase("%s сделал серию вдохов и выдохов, чтобы стать чуть трезвее и приготовился к поединку.");
            addStartPhrase("%s многозначительно обозначил свою готовность к бою, оторвав от стула ножку.");
            addStartPhrase("%s обсудил с друзьями как ему быть и что делать и занял боевую позицию.");
            addStartPhrase("Сказав, что инквизиция не пройдёт, %s размял кулаки и приготовился к драке.");
            addStartPhrase("Без лишних слов и действий %s подтвердил свою готовность к драке кивком головы.");
            addStartPhrase("%s взял в обе руки по разному напитку и приготовился метать ими в противника.");
            addStartPhrase("%s прокричал: \"Пиво богу пива! Кружки для трона из кружек!\" и приготовился к драке.");
        }

        @Override
        public void remindUser(User user) {
            try {
                CWTavernBot.INSTANCE.sendMessage(PersonalCommands.HELP.getPersonalMessage(user, getRule()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int evalFinalResult(TournamentUsers first, TournamentUsers second) {
            String voteFor = first.getUser().toString();
            int voteCount = User.getVotersForCount(voteFor);
            return first.getScore() + new Random().nextInt(151) + voteCount;
        }
    },
    CHAIR_LEG("\"Ножка от стула\"") {
        @Override
        public List<? extends Commands> getCommands() {
            return Arrays.asList(ArenaCommands.values());
        }

        @Override
        public List<String> getCommandButtons() {
            return Arrays.stream(ArenaCommands.Weapon.values()).map(ArenaCommands.Weapon::getName).collect(Collectors.toList());
        }

        @Override
        protected void init() {
            setTie("Оба бойца были долго обменивались ударами, но ни один так и не упал.\n\nОБЪЯВЛЯЕТСЯ НИЧЬЯ! Бой состоится заново!");
            setRule("Бойцы, отправляйтесь в личку к Лизе, и выбирайте оружие, которым будете сражаться!");
            addStartPhrase("Боец %s сделал свой выбор!");
        }

        public String getWinPhrase(TournamentUsers winner, TournamentUsers loser) {
            ArenaCommands.Weapon winWep = ArenaCommands.Weapon.getByNumber(winner.getScore());
            ArenaCommands.Weapon loseWep = ArenaCommands.Weapon.getByNumber(loser.getScore());
            return String.format(winWep.getWinPhrase(loseWep), winner.getUser(), loser.getUser());
        }

        @Override
        public void remindUser(User user) {
            try {
                CWTavernBot.INSTANCE.sendMessage(PersonalCommands.HELP.getPersonalMessage(user, "Выбирай, чем будешь драться!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int evalFinalResult(TournamentUsers first, TournamentUsers second) {
            String voteFor = first.getUser().toString();
            int voteCount = User.getVotersForCount(voteFor);
            ArenaCommands.Weapon winWep = ArenaCommands.Weapon.getByNumber(first.getScore());
            ArenaCommands.Weapon loseWep = ArenaCommands.Weapon.getByNumber(second.getScore());
            return winWep.against(loseWep) + winWep.getStat(first.getUser()) + voteCount;
        }
    };

    private String name;
    private String tie;
    private String rule;
    List<String> win = new ArrayList<>();
    private List<String> start = new ArrayList<>();

    TournamentType(String name) {
        this.name = name;
        init();
    }

    public abstract List<? extends Commands> getCommands();
    public List<String> getCommandButtons() {
        return Collections.emptyList();
    }

    protected void init() {
    }

    @Override
    public String toString() {
        return name;
    }

    public String getTie(TournamentUsers left, TournamentUsers right) {
        return String.format(tie, left.getUser(), right.getUser());
    }

    public void remindUser(User user) {

    }

    public String getWinPhrase(TournamentUsers winner, TournamentUsers loser) {
        return String.format(win.get(new Random().nextInt(win.size())), winner.getUser(), loser.getUser());
    }

    public String getWinPhrase(User winner, User loser) {
        return String.format(win.get(new Random().nextInt(win.size())), winner, loser);
    }

    public String getStartPhrase() {
        return start.get(new Random().nextInt(start.size()));
    }

    public String getRule() {
        return rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTie(String tie) {
        this.tie = tie;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void addWinPhrase(String phrase) {
        win.add(phrase);
    }

    public void addStartPhrase(String phrase) {
        start.add(phrase);
    }

    public abstract int evalFinalResult(TournamentUsers first, TournamentUsers second);
}
