package org.nia.logic.quests.judge;

import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Иван, 28.03.2017.
 */
public enum StolenHorse implements IQuestStep {

    GUILTY_ARM("Выразить свою позицию судье"
            , "Под тяжестью улик обвиняемая призналась в преступлении. Ты выиграл дело."
            , "Не смотря на очевидные улики против обвиняемой, ты мямлил перед судьей что-то нескладное, и женщина " +
            "была оправдана. Ты проиграл дело."),
    ARM("Спросить, что у обвиняемой с рукой"
            , "Обвиняемая отказалась показывать руку. После вмешательства охраны, обвиняемая показала синяк на плече, " +
            "по форме и размерам похожий на подкову."
            , Collections.singletonList(GUILTY_ARM)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.incWinChance(30);
        }
    },
    GUILTY_WHO("Выразить свою позицию судье"
            , "Ты указал судье на несостоятельность доводов обвиняемой о её алиби. Этого оказалось достаточно, и ты выиграл дело."
            , "Несмотря на отсутствие свидетелей, подтверждающих алиби обвиняемой, судья не счел твои аргументы " +
            "убедительными. Обвиняемая оправдана, ты проиграл дело."),
    ASK_IVAN("Вызвать на допрос дровосека"
            , "Пришедший дровосек Иван вспомнил, что два дня назад встретил в таверне очаровательную русалку, и что они " +
            "отлично провели время за игрой в покер, а потом русалка села на крылатую лошадь и улетела в свой замок. " +
            "На вопрос, похожа ли обвиняемая на его \"русалку\", дровосек затрудинлся ответить.\nОбвиняемая в гневе " +
            "начала оскорблять дровосека и махать руками, и ты обратил внимание, что её правая рука двигается немного скованно."
            , Arrays.asList(ARM, GUILTY_WHO)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.incWinChance(10);
        }
    },
    ASK_LISA("Вызвать на допрос официантку"
            , "Пришедшая Лиза заявила, что у нее смены длятся практически круглосуточно, поэтому никого кроме барменов " +
            "она не помнит, и ничем помочь не может.\nОбвиняемая в гневе начала оскорблять Лизу и махать руками, и ты " +
            "обратил внимание, что её правая рука двигается немного скованно."
            , Arrays.asList(ARM, GUILTY_WHO)),
    ASK_WHO("- Кто может подтвердить ваши слова?"
            , "- Ну, там было много посетителей, но наверняка меня запомнила официантка Лиза! Еще там был какой-то " +
            "рыжеволосый мужик, который ко мне приставал, но я его отшила.\n\n-Ваша честь, мужчина, о котором " +
            "говорит обвиняемая - это дровосек Иван, он каждый день напивается в таверне, пока не отключится."
            , Arrays.asList(ASK_LISA, ASK_IVAN)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.incWinChance(10);
        }
    },
    GUILTY_WHERE("Выразить свою позицию судье"
            , "Ты заявил, что злодейская внешность и неуверенный ответ " +
            "женщины о месте пребывания в момент кражи подтвержает её вину. Ну а уж то, что отряды самообороны - " +
            "попрошайки и воры, известно каждому.\nАдвокат обвиняемой лепетал что-то о свидетелях, но он смотрелся " +
            "откровенно жалко, и на его фоне твои аргументы показались не такими и уж надуманными. Ты убедил судью и выиграл дело."
            , "Судья долго буравил тебя взглядом, как будто хотел понять, серьезно ли ты пытаешься убедить его в " +
            "такой чуши.\nТы не смог убедить судью в виновности женщины и проиграл дело."),
    ASK_WHERE("- Где вы были в ночь кражи?"
            , "Женщина неуверенно отвечает:\n" +
            "- Я была в таверне, пила сивуху, как обычно."
            , Arrays.asList(ASK_WHO, GUILTY_WHERE)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.incWinChance(10);
        }
    },
    GUILTY_WITHOUT_INFO("Выразить свою позицию судье"
            , "Тебе даже не пришлось допрашивать обвиняемую. Тебе удалось убедить судью, что злодейская внешность " +
            "женщины подтвержает её вину.\n\n" +
            "Видимо, судья сегодня был с большого бодуна, раз тебе удалось убедить его такими слабыми аргументами."
            , "Твои аргументы о злодейской внешности женщины были подняты на смех.\nТы не смог убедить судью в " +
            "виновности обвиняемой и проиграл дело."),
    INIT("", "Тебе досталось дело о краже боевого скакуна. Обвиняемая пришла со своим адвокатом, так что ты будешь " +
            "исполнять роль прокурора.\nПреступление произошло два дня назад, примерно в середине ночи. Хозяин скакуна " +
            "заявил что видел как женщина, похожая на обвиняемую, крутилась весь день неподалеку от его конюшни. " +
            "Обвиняемая - молодая женщина, судя по всему, из какой-то дружины местных сил " +
            "самообороны - она одета в потрепанную армейскую форму старого образца. Лицо жесткое, не выражающее " +
            "никаких эмоций.\n\nСудья начинает процесс, тебе необходимо допросить обвиняемую и высказать " +
            "судье свою позицию. Чем более убедительным ты будешь, тем больше шансов на успех."
            , Arrays.asList(ASK_WHERE, GUILTY_WITHOUT_INFO)) {
        @Override
        public void doWork(QuestEvent questEvent) {
            questEvent.setWinChance(30);
        }
    };
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    StolenHorse(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    StolenHorse(String command, String text, List<IQuestStep> next) {
        this.command = command;
        this.text = text;
        this.next.addAll(next);
    }

    @Override
    public String getText(Quest quest) {
        return text;
    }

    @Override
    public List<IQuestStep> getNext(Quest quest) {
        return next;
    }

    @Override
    public String getCommand(String formatParam) {
        return command;
    }

    @Override
    public String getGoodText() {
        return goodText;
    }

    @Override
    public String getBadText() {
        return badText;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public IQuestEvent getIQuest() {
        return JudgeQuest.JudgeEvent.JUDGE_STOLEN_HORSE;
    }
}
