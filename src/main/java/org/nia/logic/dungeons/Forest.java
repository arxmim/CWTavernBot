package org.nia.logic.dungeons;

/**
 * @author Иван, 15.03.2017.
 */
public enum Forest {
    CAVE("Осмотреть пещеру", "В пещере явно живет ведьма. Повсюду лежат"),
    LONG_BEAR("Продолжить двигаться по тропе", "Вы двигались по тропе, пока она не закончилась. Внезапно, вы услышали " +
            "рев где-то недалеко. Вы двинулись в сторону шума и наткнулись на медведя!", "Вы победили медведя и собрали его когти"),
    SHORT_BEAR("Пройти по восточной тропе", "Вым уже начало казаться, что эта тропа никогда не закончится, как вдруг на вас напал медведь!", "Вы победили медведя и собрали его когти"),
    CAVE_ENTER("Пройти по западной тропе", "Вы увидели невдалеке от тропы пещеру, на входе в пещеру лежат выбеленные на солнце кости", CAVE, LONG_BEAR),
    FIRST("Пройти вглубь леса", "Вы начали двигаться по лесу. Деревья становились все выше, солнечный свет исчез, " +
            "сменившись жутковатым полумраком. Через некоторое время вы вышли на звериную тропу, которая вывела вас " +
            "на небольшую поляну. Вы уже хотели было двигаться дальше, как вдруг из-за деревьев на вас набросились два " +
            "гигантских паука!", "Вы убили всех пауков и заметили, что с поляны ведет две отчетливых тропы", CAVE_ENTER, SHORT_BEAR),
    INIT("", "Ты с товарищами дошел до опушки леса. Впереди вам предстоит нешуточный бой, поэтому тебе стоит " +
            "договариваться с друзьями, о том, как вы будете действовать. Для этого ты можешь просто писать текст в чат", FIRST);

    private String button;
    private String text;
    private String winText;
    private boolean battle;

    Forest(String button, String text, Forest... next) {
        this.text = text;
        this.button = button;
        battle = false;
    }
    Forest(String button, String text, String winText, Forest... next) {
        this.text = text;
        this.button = button;
        this.winText = winText;
        battle = true;
    }
}
