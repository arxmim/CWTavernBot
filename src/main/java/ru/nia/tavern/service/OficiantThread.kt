package ru.nia.tavern.service

import org.apache.commons.lang3.time.DateUtils
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Dancing
import ru.nia.tavern.model.Tournament
import ru.nia.tavern.model.User
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Иван, 09.03.2017.
 */
class OficiantThread(private val bot: CWTavernBot) : Thread() {

    @Volatile
    private var barmenCommand: Boolean = false
    @Volatile
    private var tournamentPhase: Boolean = false

    init {
        isDaemon = true
        INSTANCE = this
    }

    override fun run() {
        var now = Date()
        while (true) {
            try {
                val gcWas = GregorianCalendar()
                gcWas.time = now
                TimeUnit.SECONDS.sleep(15)
                now = Date()
                val current = Tournament.current
                if ((current == null || !current.isInProgress) && (timedStart(gcWas) || barmenCommand)) {
                    barmenCommand = false
                    serve()
                } else if (current != null && (tournamentInterval(gcWas, current) || tournamentPhase)) {
                    tournamentPhase = false
                    val answerList = current.work()
                    for (answer in answerList) {
                        bot.sendMessage(ServingMessage.getTournamentMessage(answer))
                    }
                }
                Dancing.getAllCurrent().forEach(???({ it.process() }))
                val gcNow = GregorianCalendar()
                gcNow.time = now
                if (!DateUtils.isSameDay(gcWas, gcNow) && gcNow.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    User.getAll().forEach { user ->
                        user.setDrinkedWeek(0)
                        user.save()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    private fun tournamentInterval(gcWas: GregorianCalendar, current: Tournament): Boolean {
        val gcNow = GregorianCalendar()
        val gcRegistration = GregorianCalendar()
        gcRegistration.time = current.getRegistrationDateTime()
        gcNow.time = Date()
        var tournamentPhase = true
        if (current.isAnnounced) {
            tournamentPhase = gcNow.time.after(gcRegistration.time)
        } else if (current.isRegistration) {
            gcRegistration.add(Calendar.MINUTE, Tournament.REGISTRATION_TIME)
            tournamentPhase = gcNow.time.after(gcRegistration.time)
        }
        return tournamentPhase && gcWas.get(GregorianCalendar.MINUTE) < gcNow.get(GregorianCalendar.MINUTE)
    }

    private fun timedStart(gcWas: GregorianCalendar): Boolean {
        val gcNow = GregorianCalendar()
        gcNow.time = Date()
        val INIT_MINUTE = 10
        val INTERVAL = 20
        return gcWas.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE - 1 && gcNow.get(GregorianCalendar.MINUTE) % INTERVAL == INIT_MINUTE
    }

    private fun serve() {
        val users = User.getAll()
        val servedDrink = ArrayList<User>()
        val servedFood = ArrayList<User>()
        users.stream().filter({ usr -> usr.wanted != null || usr.wantedFood != null }).forEach { usr ->
            if (usr.wanted != null) {
                usr.setDrinkType(usr.wanted)
                usr.alkoCount = 2
                usr.setWanted(null)
                usr.save()
                servedDrink.add(usr)
            }
            if (usr.wantedFood != null) {
                usr.setFood(usr.wantedFood)
                usr.foodCount = 1
                usr.setWantedFood(null)
                usr.save()
                servedFood.add(usr)
            }
        }
        try {
            if (!servedDrink.isEmpty() || !servedFood.isEmpty()) {
                bot.sendMessage(ServingMessage.getMessage(servedDrink, servedFood))
            }
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

    }

    fun setBarmenCommand(barmenCommand: Boolean) {
        this.barmenCommand = barmenCommand
    }

    fun setTournamentPhase(tournamentPhase: Boolean) {
        this.tournamentPhase = tournamentPhase
    }

    companion object {
        var INSTANCE: OficiantThread? = null
    }
}
