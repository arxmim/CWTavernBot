package ru.nia.tavern.service

import org.nia.strings.Emoji
import ru.nia.tavern.model.DrinkPref
import ru.nia.tavern.model.User
import ru.nia.tavern.model.repository.DrinkPrefRepository
import ru.nia.tavern.model.repository.UserRepository
import ru.nia.tavern.model.types.DrinkType
import java.util.*

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
class UserServingService {
    private var userRepository: UserRepository
    private var drinkPrefRepository: DrinkPrefRepository

    constructor(userRepository: UserRepository, drinkPrefRepository: DrinkPrefRepository) {
        this.userRepository = userRepository
        this.drinkPrefRepository = drinkPrefRepository
    }


    fun incDrink(user: User, dt: DrinkType, count: Int) {
        val drinkPref = getOrCreateDrinkPref(user, dt)
        drinkPref.incToDrink(count)
        drinkPrefRepository.save(drinkPref)
    }

    fun incThrow(user: User, dt: DrinkType) {
        val drinkPref = getOrCreateDrinkPref(user, dt)
        drinkPref.incToThrow()
        drinkPrefRepository.save(drinkPref)
    }

    fun incToBeThrown(user: User, dt: DrinkType) {
        val drinkPref = getOrCreateDrinkPref(user, dt)
        drinkPref.incToBeThrown()
        drinkPrefRepository.save(drinkPref)
    }

    private fun getOrCreateDrinkPref(user: User, dt: DrinkType): DrinkPref {
        return Optional.ofNullable(drinkPrefRepository.findByUserAndDrinkType(user, dt))
                .orElseGet {
                    DrinkPref(user, dt)
                }
    }

    fun getFightClubStatsSum(user: User): Int {
        var prefs = drinkPrefRepository.findByUser(user)
        return getStr(prefs) + getAgi(prefs) + getCon(prefs) + getCha(prefs) + getKno(user)
    }

    fun fightClubStats(user: User): String {
        var prefs = drinkPrefRepository.findByUser(user)
        return """
          |Твои характеристики:
          |${Emoji.STR}Сила: ${getStr(prefs)}
          |${Emoji.AGI}Ловкость: ${getAgi(prefs)}
          |${Emoji.CHA}Обаяние: ${getCha(prefs)}
          |${Emoji.CON}Стойкость: ${getCon(prefs)}
          |${Emoji.KNO}Знание таверны: ${getStr(prefs)}
          |${Emoji.STR}Сила: ${getKno(user)}
        """.trimMargin()
    }

    fun publicFightClubStats(user: User): String {
        var prefs = drinkPrefRepository.findByUser(user)

        return """
          |Твои характеристики:
          |${Emoji.STR}Сила: ${roundStatToString(getStr(prefs))}
          |${Emoji.AGI}Ловкость: ${roundStatToString(getAgi(prefs))}
          |${Emoji.CHA}Обаяние: ${roundStatToString(getCha(prefs))}
          |${Emoji.CON}Стойкость: ${roundStatToString(getCon(prefs))}
          |${Emoji.KNO}Знание таверны: ${roundStatToString(getStr(prefs))}
          |${Emoji.STR}Сила: ${roundStatToString(getKno(user))}
        """.trimMargin()
    }

    private fun roundStatToString(stat: Int): String {
        val res: String
        if (stat < 4) {
            res = "чуть меньше чем ничего"
        } else if (stat < 6) {
            res = "тебя превосходят даже голуби"
        } else if (stat < 8) {
            res = "как у ребенка"
        } else if (stat < 10) {
            res = "так мало, что даже стыдно"
        } else if (stat < 13) {
            res = "низко"
        } else if (stat < 17) {
            res = "чуть ниже нормы"
        } else if (stat < 21) {
            res = "нормально"
        } else if (stat < 25) {
            res = "выше среднего"
        } else if (stat < 29) {
            res = "высоко"
        } else if (stat < 34) {
            res = "очень высоко"
        } else if (stat < 38) {
            res = "практически нет равных"
        } else if (stat < 43) {
            res = "нет равных"
        } else if (stat < 48) {
            res = "почти как у бога"
        } else if (stat < 55) {
            res = "почти божественно"
        } else if (stat < 60) {
            res = "божественно"
        } else {
            res = "превосходит богов"
        }
        return res
    }

    fun getStr(prefs: List<DrinkPref>): Int {
        return 1 + Math.sqrt(prefs.stream()
                .filter { e ->
                    Arrays.asList(DrinkType.AVE_WHITE, DrinkType.BEER, DrinkType.GHOST)
                            .contains(e.drinkType)
                }
                .mapToInt { it.toDrinkNormalized }.sum().toDouble()).toInt()
    }

    fun getAgi(prefs: List<DrinkPref>): Int {
        return 1 + Math.sqrt(prefs.stream().mapToInt { it.toThrow }.sum().toDouble()).toInt()
    }

    fun getCha(prefs: List<DrinkPref>): Int {
        var charism = 1
        charism += Math.sqrt(prefs.stream()
                .filter { e ->
                    Arrays.asList(DrinkType.CHLEN, DrinkType.RED_POWER, DrinkType.MORDOR)
                            .contains(e.drinkType)
                }
                .mapToInt { it.toDrinkNormalized }.sum().toDouble()).toInt()
        return charism
    }

    fun getCon(prefs: List<DrinkPref>): Int {
        var constitution = 1
        constitution += Math.sqrt(prefs.stream().mapToInt { e ->
            var res = e.toBeThrown
            if (e.drinkType === DrinkType.MANDARINE) {
                res += e.toDrinkNormalized
            }
            res
        }.sum().toDouble()).toInt()
        return constitution
    }

    fun getKno(user: User): Int {
        return Math.sqrt(user.drinkedTotal.toDouble()).toInt() / 2
    }


}