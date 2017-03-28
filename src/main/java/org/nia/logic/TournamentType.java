package org.nia.logic;

import org.nia.logic.commands.Commands;
import org.nia.logic.commands.FightClubCommands;
import org.nia.logic.commands.PostukCommands;
import org.nia.model.TournamentUsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
            addStartPhrase("Cказав, что инквизиция не пройдёт, %s размял кулаки и приготовился к драке.");
            addStartPhrase("Без лишних слов и действий %s подтвердил свою готовность к драке кивком головы.");
            addStartPhrase("%s взял в обе руки по разному напитку и приготовился метать ими в противника.");
            addStartPhrase("%s прокричал: \"Пиво богу пива! Кружки для трона из кружек!\" и приготовился к драке.");
        }

        @Override
        public int evalFinalResult(int score) {
            return score + new Random().nextInt(81);
        }
    },
    POSTUK("\"ПОСТУК\"") {
        @Override
        public List<? extends Commands> getCommands() {
            return Arrays.asList(PostukCommands.values());
        }

        @Override
        protected void init() {
            setTie("Оба бойца были долго обменивались ударами, но ни один так и не упал.\n\nОБЪЯВЛЯЕТСЯ НИЧЬЯ! Бой состоится заново!");
            setRule("Бойцы, отправляйтесь в личку к Лизе, и выбирайте оружие, которым будете сражаться!");
            addStartPhrase("Боец %s сделал свой выбор!");
        }

        public String getWinPhrase(TournamentUsers winner, TournamentUsers loser) {
            return String.format(win.get(new Random().nextInt(win.size())), winner.getUser(), loser.getUser());
        }

        @Override
        public int evalFinalResult(int score) {
            return score + new Random().nextInt(81);
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

    protected void init() {
    }

    @Override
    public String toString() {
        return name;
    }

    public String getTie(TournamentUsers left, TournamentUsers right) {
        return String.format(tie, left.getUser(), right.getUser());
    }

    public String getWinPhrase(TournamentUsers winner, TournamentUsers loser) {
        return String.format(win.get(new Random().nextInt(win.size())), winner.getUser(), loser.getUser());
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

    public abstract int evalFinalResult(int score);
}
