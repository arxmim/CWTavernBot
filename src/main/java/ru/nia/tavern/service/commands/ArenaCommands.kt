package ru.nia.tavern.service.commands

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.DrinkPref
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.User
import ru.nia.tavern.service.CWTavernBot
import java.util.*

/**
 * @author IANazarov
 */
enum class ArenaCommands private constructor(protected var text: String) : Commands {
    WEAPON("") {
        override fun isApplicable(message: Message, from: User): Boolean {
            val currentByUserID = TournamentUsers.getCurrentByUserID(message.from.id!!)
            return currentByUserID != null && currentByUserID.inFight && Weapon.getByName(message.text) != null
        }

        override fun apply(message: Message, from: User): String {
            val currentByUserID = TournamentUsers.getCurrentByUserID(message.from.id!!)
            val user = currentByUserID!!.user
            val weapon = Weapon.getByName(message.text)
            currentByUserID.score = weapon!!.number
            currentByUserID.save()

            try {
                val sendMessage = ServingMessage.getTournamentMessage(String.format(currentByUserID.tournament.type.startPhrase + "\nКоличество болельщиков: 0", user))
                setKeyboard(currentByUserID.user, sendMessage)
                CWTavernBot.INSTANCE.sendMessage(sendMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

            //            Pair<TournamentUsers, TournamentUsers> twoUsers = TournamentUsers.getTwoUsers(currentByUserID.getTournament());
            //            TournamentUsers left = twoUsers.getLeft();
            //            TournamentUsers right = twoUsers.getRight();
            //            Weapon leftWep = Weapon.getByNumber(left.getScore());
            //            Weapon rightWep = Weapon.getByNumber(right.getScore());
            //            if (leftWep != null && rightWep != null) {
            //                try {
            //                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(String.format(leftWep.getReadyPhrase(), left.getUser(), left.getUser().roundStatToString(leftWep.getStat(left.getUser())))));
            //                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(String.format(rightWep.getReadyPhrase(), right.getUser(), right.getUser().roundStatToString(rightWep.getStat(right.getUser())))));
            //                } catch (TelegramApiException e) {
            //                    e.printStackTrace();
            //                }
            //            }
            return "Ты сделал хороший выбор. Теперь возвращайся в таверну и следи за результатом боя!"
        }
    };

    protected fun setKeyboard(user: User, sendMessage: SendMessage) {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()
        val button = InlineKeyboardButton()
        button.text = "AVE_" + user.toString().replace("@", "").toUpperCase()
        button.callbackData = user.userID.toString()
        row.add(button)
        keyboard.add(row)
        inlineKeyboardMarkup.keyboard = keyboard
        sendMessage.replyMarkup = inlineKeyboardMarkup
    }

    override fun isApplicable(message: Message, from: User): Boolean {
        return message.text.contains(this.text)
    }

    override fun apply(message: Message, from: User): String {
        return ""
    }

    enum class Weapon private constructor(number: Int, name: String) {
        CHAIR(1, "Ножка от стула") {
            override fun init() {
                addSameWeaponPhrase("Оба бойца решили драться ножками от стульев, но %s оказался #чутьлучше чем %s. Чистая победа!")
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своими ножками от стульев. Удача сегодня на стороне %s, он без труда расправился с %s.")
                readyPhrase = "Боец %s будет сражаться ножкой от стула.\nБонус знания таверны: %s"
            }

            override fun against(another: Weapon): Int {
                if (another === KARATE) {
                    return halfWin
                } else if (another === ARM) {
                    return fullWin
                }
                return 0
            }

            override fun getWinPhrase(another: Weapon): String {
                if (another === CHAIR) {
                    return getSameWeaponPhrase()
                } else if (another === KARATE) {
                    return "%s буквально разбил лицо %s своей ножкой от стула. Запомните, карате не работает против ножки от стула!"
                } else if (another === ARM) {
                    return "%s нанес противнику удар ножкой от стула, а в ответ отхватил удар кулаком. Так продолжалось " + "до тех пор, пока %s не упал, не в силах больше сражаться. Ножка от стула тащит!"
                } else if (another === CAPOEIRA) {
                    return "Обычно капоэйра помогает против бродяг с ножками от стульев, но не в этот раз. %s " + "оказался слишком опытным бойцом таверны, и без труда уложил незадачливого танцора %s."
                } else if (another === MUG) {
                    return "%s знает каждый угол в этой таверне, и смог добежать до противника, уклонившись от всех " + "бросков жбанами. Ну а в ближнем бою ножка от стула не оставила %s шанса."
                }
                return ""
            }

            override fun getStat(user: User): Int {
                return user.kno
            }
        }, //kno
        KARATE(2, "Карате") {
            override fun init() {
                addSameWeaponPhrase("Оба бойца решили драться при помощи карате, но %s оказался #чутьлучше чем %s. Чистая победа!")
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своим карате. Удача сегодня на стороне %s, он без труда расправился с %s.")
                readyPhrase = "Боец %s будет драться при помощи карате.\nБонус ловкости: %s"
            }

            override fun against(another: Weapon): Int {
                if (another === ARM) {
                    return halfWin
                } else if (another === CAPOEIRA) {
                    return fullWin
                }
                return 0
            }

            override fun getWinPhrase(another: Weapon): String {
                if (another === CHAIR) {
                    return "Сейчас вы наблюдаете удивительно редкую картину - карате %s оказалось сильнее ножки от стула! Победа над бойцом %s."
                } else if (another === KARATE) {
                    return getSameWeaponPhrase()
                } else if (another === ARM) {
                    return "Карате круче кулаков, это знает каждый. %s с легкостью нокаутировал %s."
                } else if (another === CAPOEIRA) {
                    return "Карате против капоэйры, восток против запада, сила против обаяния! Увы, чуда не произошло и %s надрал задницу %s."
                } else if (another === MUG) {
                    return "Наш каратист %s отбил жбан в соперника, и %s свалился, пораженный своим же снарядом. Неожиданный итог боя!"
                }
                return ""
            }

            override fun getStat(user: User): Int {
                val prefs = DrinkPref.getByUser(user)
                return user.getAgi(prefs)
            }
        }, //agi
        ARM(3, "Кулаки") {
            override fun init() {
                addSameWeaponPhrase("Оба бойца решили драться кулаками, но %s оказался #чутьлучше чем %s. Чистая победа!")
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться крепостью своих кулаков. Удача сегодня на стороне %s, он без труда расправился с %s.")
                readyPhrase = "Боец %s будет драться кулаками.\nБонус стойкости: %s"
            }

            override fun against(another: Weapon): Int {
                if (another === CAPOEIRA) {
                    return halfWin
                } else if (another === MUG) {
                    return fullWin
                }
                return 0
            }

            override fun getWinPhrase(another: Weapon): String {
                if (another === CHAIR) {
                    return "%s удалось доказать сопернику, что правильные кулаки работают даже против ножки от стула. %s побежден!"
                } else if (another === KARATE) {
                    return "%s отвесил такого смачного леща сопернику, что у %s искры из глаз посыпались. Сегодня мы наблюдаем тот редкий случай, когда карате оказалось бессильно против простых кулаков!"
                } else if (another === ARM) {
                    return getSameWeaponPhrase()
                } else if (another === CAPOEIRA) {
                    return "Наш любитель капоэйры долго танцевал вокруг %s, пока не был им пойман и жестоко избит! %s в другой раз выбирай серьезный способ борьбы!"
                } else if (another === MUG) {
                    return "%s поймал лицом пару жбанов, прежде чем дошел до соперника, но он все же дошел, и %s пришлось отскребать от стены."
                }
                return "%s победил бойца %s. Текст еще не написан."
            }

            override fun getStat(user: User): Int {
                val prefs = DrinkPref.getByUser(user)
                return user.getCon(prefs)
            }
        }, //con
        CAPOEIRA(4, "Капоэйра") {
            override fun init() {
                addSameWeaponPhrase("Оба бойца решили сражаться, используя искусство капоэйры, но %s оказался #чутьлучше чем %s. Чистая победа!")
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своей капоэйрой. Удача сегодня на стороне %s, он без труда расправился с %s.")
                readyPhrase = "Боец %s будет сражаться при помощи капоейры.\nБонус обаяния: %s"
            }

            override fun against(another: Weapon): Int {
                if (another === MUG) {
                    return halfWin
                } else if (another === CHAIR) {
                    return fullWin
                }
                return 0
            }

            override fun getWinPhrase(another: Weapon): String {
                if (another === CHAIR) {
                    return "Очаровательный %s своим танцем отвлек соперника, и пока тот вспоминал, зачем ему ножка от стула, решительно вырубил %s ударом пяткой в ухо"
                } else if (another === KARATE) {
                    return "%s плавными движениями задницы нарушил душевное равновесие каратиста %s и смог нокаутировать соперника!"
                } else if (another === ARM) {
                    return "Капоэйра %s оказалась #чутьсильнее кулаков %s. Неожиданный результат!"
                } else if (another === CAPOEIRA) {
                    return getSameWeaponPhrase()
                } else if (another === MUG) {
                    return "%s обещал показать искусство капоэйры, а вместо этого съел красную пилюлю и нечеловеческим образом уклонился от всех летевших в него жбанов. %s признал свое поражение!"
                }
                return "%s победил бойца %s. Текст еще не написан."
            }

            override fun getStat(user: User): Int {
                val prefs = DrinkPref.getByUser(user)
                return user.getCha(prefs)
            }
        }, // cha
        MUG(5, "Метать жбаны") {
            override fun init() {
                addSameWeaponPhrase("Оба бойца решили метать жбаны, но %s оказался #чутьлучше чем %s. Чистая победа!")
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться точностью в метании жбанов. Удача сегодня на стороне %s, он без труда расправился с %s.")
                readyPhrase = "Боец %s будет метать жбаны в противника.\nБонус силы: %s"
            }

            override fun getStat(user: User): Int {
                val prefs = DrinkPref.getByUser(user)
                return user.getStr(prefs)
            }

            override fun against(another: Weapon): Int {
                if (another === CHAIR) {
                    return halfWin
                } else if (another === KARATE) {
                    return fullWin
                }
                return 0
            }

            override fun getWinPhrase(another: Weapon): String {
                if (another === CHAIR) {
                    return "Метатель жбанов %s в очередной раз доказал, что дальнобойное оружие лучше ножки от стула. %s повержен!"
                } else if (another === KARATE) {
                    return "Точный бросок жбаном в лицо принес %s победу над %s! И никакое карате не помогло!"
                } else if (another === ARM) {
                    return "Кулачные бойцы отличаются большой стойкостью, но что поделать, если в тебя прилетел жбан? %s нокаутировал противника, не дав %s сделать и шага!"
                } else if (another === CAPOEIRA) {
                    return "Что может быть прекраснее вида разбитого носа любителя капоэйры? У метателя жбанов %s сегодня счастливый день, его соперник %s повержен!"
                } else if (another === MUG) {
                    return getSameWeaponPhrase()
                }
                return "%s победил бойца %s. Текст еще не написан."
            }
        };
        //str

        private val sameWeaponPhrase = ArrayList<String>()
        internal var fullWin = 30
        internal var halfWin = 15
        var readyPhrase: String
            internal set
        var number: Int = 0
            internal set
        var name: String
            internal set

        init {
            this.number = number
            this.name = name
            init()
        }

        fun addSameWeaponPhrase(text: String) {
            sameWeaponPhrase.add(text)
        }

        internal fun getSameWeaponPhrase(): String {
            return if (sameWeaponPhrase.isEmpty()) {
                ""
            } else sameWeaponPhrase[Random().nextInt(sameWeaponPhrase.size)]
        }

        abstract fun getWinPhrase(another: Weapon): String

        abstract fun init()

        abstract fun against(loseWep: Weapon): Int

        abstract fun getStat(user: User): Int

        companion object {

            fun getByNumber(num: Int): Weapon? {
                for (w in values()) {
                    if (w.number == num) {
                        return w
                    }
                }
                return null
            }

            fun getByName(name: String): Weapon? {
                for (w in values()) {
                    if (w.name == name) {
                        return w
                    }
                }
                return null
            }
        }
    }
}
