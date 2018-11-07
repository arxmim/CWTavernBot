package ru.nia.tavern.model.types

import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.User
import ru.nia.tavern.service.CWTavernBot
import ru.nia.tavern.service.commands.ArenaCommands
import ru.nia.tavern.service.commands.Commands
import ru.nia.tavern.service.commands.FightClubCommands
import ru.nia.tavern.service.commands.PersonalCommands
import java.util.*
import java.util.stream.Collectors

/**
 * @author Иван, 11.03.2017.
 */
enum class TournamentType private constructor(var tournamentName: String?) {
    FIGHT_CLUB("\"Бойцовский клуб\"") {
        override val commands: List<Commands>
            get() = Arrays.asList(*FightClubCommands.values())

        override fun init() {
            setTie("Боец %s точным ударом пятки в глаз сбил соперника с ног. Падая, %s пнул обидчика чуть пониже спины и опрокинул его. " + "Оба оказались настолько пьяны, что не смогли встать.\n\n ОБЪЯВЛЯЕТСЯ НИЧЬЯ! Бой состоится заново!")
            rule = "Жмите /DRAKA и пусть победит сильнейший!"
            addWinPhrase("%s быстро и решительно нокаутировал %s точным ударом. Чистая победа!")
            addWinPhrase("%s оказался #чутьсильнее %s. Возможно дело было в ножке от стула, но это не точно.")
            for (dt in DrinkType.values()) {
                addWinPhrase("%s оказался #чутьсильнее %s. Возможно это из-за выпитого " + dt.command + " перед боем, но это не точно.")
                addStartPhrase("Прокричав, что " + dt.command + " уже не тот, %s метнул стакан в группу поддержки противника и обозначил свою готовность.")
                addStartPhrase("%s сказал всем, что готов к драке и нальёт всем " + dt.command + ", если победит.")
            }
            addWinPhrase("Лизонька крикнула, что любит %s и готова наливать ему каждый день! У %s разбилось сердце.")
            addWinPhrase("В последний момент бойцы передумали сражаться и решили определить победителя партией в шахматы. В итоге %s засунул ферзя бойцу %s в, гм, ухо и победил!")
            addWinPhrase("%s достал свою рапиру и заколол %s. Даже ПМС не помог!")
            addWinPhrase("Я твою мамку в кино водил! - заявил %s. Безоговорочная победа над бойцом %s!")
            addStartPhrase("%s сделал серию вдохов и выдохов, чтобы стать чуть трезвее и приготовился к поединку.")
            addStartPhrase("%s многозначительно обозначил свою готовность к бою, оторвав от стула ножку.")
            addStartPhrase("%s обсудил с друзьями как ему быть и что делать и занял боевую позицию.")
            addStartPhrase("Сказав, что инквизиция не пройдёт, %s размял кулаки и приготовился к драке.")
            addStartPhrase("Без лишних слов и действий %s подтвердил свою готовность к драке кивком головы.")
            addStartPhrase("%s взял в обе руки по разному напитку и приготовился метать ими в противника.")
            addStartPhrase("%s прокричал: \"Пиво богу пива! Кружки для трона из кружек!\" и приготовился к драке.")
        }

        override fun remindUser(user: User) {
            try {
                CWTavernBot.INSTANCE.sendMessage(PersonalCommands.HELP.getPersonalMessage(user, rule))
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        }

        override fun evalFinalResult(first: TournamentUsers, second: TournamentUsers): Int {
            val voteFor = first.user.userID.toString()
            val voteCount = User.getVotersForCount(voteFor)
            return first.score + Random().nextInt(151) + voteCount
        }
    },
    CHAIR_LEG("\"Ножка от стула\"") {
        override val commands: List<Commands>
            get() = Arrays.asList(*ArenaCommands.values())

        override val commandButtons: List<String>
            get() = Arrays.stream<ArenaCommands.Weapon>(ArenaCommands.Weapon.values()).map<String>(Function<ArenaCommands.Weapon, String> { it.getName() }).collect<List<String>, Any>(Collectors.toList())

        override fun init() {
            setTie("Оба бойца были долго обменивались ударами, но ни один так и не упал.\n\nОБЪЯВЛЯЕТСЯ НИЧЬЯ! Бой состоится заново!")
            rule = "Бойцы, отправляйтесь в личку к Лизе, и выбирайте оружие, которым будете сражаться!"
            addStartPhrase("Боец %s сделал свой выбор!")
        }

        override fun getWinPhrase(winner: TournamentUsers, loser: TournamentUsers): String {
            val winWep = ArenaCommands.Weapon.getByNumber(winner.score)
            val loseWep = ArenaCommands.Weapon.getByNumber(loser.score)
            return String.format(winWep!!.getWinPhrase(loseWep), winner.user, loser.user)
        }

        override fun remindUser(user: User) {
            try {
                CWTavernBot.INSTANCE.sendMessage(PersonalCommands.HELP.getPersonalMessage(user, "Выбирай, чем будешь драться!"))
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        }

        override fun evalFinalResult(first: TournamentUsers, second: TournamentUsers): Int {
            val voteFor = first.user.userID.toString()
            val voteCount = User.getVotersForCount(voteFor)
            val winWep = ArenaCommands.Weapon.getByNumber(first.score)
            val loseWep = ArenaCommands.Weapon.getByNumber(second.score)
            return winWep!!.against(loseWep) + winWep.getStat(first.user) + voteCount
        }
    };

    private var tie: String? = null
    var rule: String? = null
    internal var win: MutableList<String> = ArrayList()
    private val start = ArrayList<String>()

    abstract val commands: List<Commands>
    val commandButtons: List<String>
        get() = emptyList()

    val startPhrase: String
        get() = start[Random().nextInt(start.size)]

    init {
        init()
    }

    protected open fun init() {}

    override fun toString(): String? {
        return tournamentName
    }

    fun getTie(left: TournamentUsers, right: TournamentUsers): String {
        return String.format(tie, left.user, right.user)
    }

    open fun remindUser(user: User) {

    }

    open fun getWinPhrase(winner: TournamentUsers, loser: TournamentUsers): String {
        return String.format(win[Random().nextInt(win.size)], winner.user, loser.user)
    }

    fun getWinPhrase(winner: User, loser: User): String {
        return String.format(win[Random().nextInt(win.size)], winner, loser)
    }

    fun setTie(tie: String) {
        this.tie = tie
    }

    fun addWinPhrase(phrase: String) {
        win.add(phrase)
    }

    fun addStartPhrase(phrase: String) {
        start.add(phrase)
    }

    abstract fun evalFinalResult(first: TournamentUsers, second: TournamentUsers): Int
}
