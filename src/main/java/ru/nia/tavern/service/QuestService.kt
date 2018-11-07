package ru.nia.tavern.service

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.time.DateUtils
import org.nia.strings.Emoji
import org.springframework.scheduling.annotation.Scheduled
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.exceptions.TelegramApiRequestException
import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.User
import ru.nia.tavern.model.repository.QuestEventRepository
import ru.nia.tavern.model.repository.QuestRepository
import ru.nia.tavern.model.repository.UserRepository
import ru.nia.tavern.model.types.Location
import ru.nia.tavern.quests.ICrossQuestStep
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Slf4j
class QuestService {

    private var bot: CWTavernBot
    private var questRepository: QuestRepository
    private var questEventRepository: QuestEventRepository
    private var userRepository: UserRepository

    constructor(bot: CWTavernBot, questRepository: QuestRepository, questEventRepository: QuestEventRepository, userRepository: UserRepository) {
        this.bot = bot
        this.questRepository = questRepository
        this.questEventRepository = questEventRepository
        this.userRepository = userRepository
    }


    fun evalReward(quest: Quest): Int {
        val START_SUM = 1
        val all = QuestEvent.getAll(quest)
        var sum = all.stream().mapToInt { e ->
            if (e.win == true) {
                return@mapToInt e.iQuestEvent.reward
            } else {
                return@mapToInt -e.iQuestEvent.reward
            }
        }.sum()
        val returnTime = quest.returnTime!!
        val startTime = quest.startTime

        val duration = TimeUnit.MILLISECONDS.toSeconds(returnTime.time - startTime.time).toInt()
        if (duration > 10) {
            val progressInterval = duration / 10
            sum += START_SUM * progressInterval
        }
        if (sum < 0) {
            sum = 0
        }
        return sum
    }

    @Scheduled(fixedDelay = 5_000)
    fun check() {
        try {
            val now = Date()
            for (usr in User.all) {
                if (usr.onQuest()) {
                    val quest = questRepository.findTop1ByUserAndReturnTimeIsNull(usr)
                    var event = questEventRepository.findTop1ByQuestAndWinIsNull(quest!!)
                    if (event != null && event.eventTime!!.before(DateUtils.addMinutes(now, -30))) {
                        val linkedEvent = event.linkedQuestEvent
                        var badText = event.step.getBadText(quest)
                        if (linkedEvent != null) {
                            badText = (event.step as ICrossQuestStep).badInactiveText
                            val linkedQuest = linkedEvent.quest
                            val goodText = (linkedEvent.step as ICrossQuestStep).goodInactiveText + "\n\nУдачное решение! Твоя награда за задание будет увеличена."
                            linkedEvent.win = true
                            linkedEvent.step.doFinal(linkedEvent)
                            questEventRepository.save(linkedEvent)
                            linkedQuest.eventTime = linkedQuest.questEnum.getNextEventTime(linkedQuest)
                            questEventRepository.save(linkedEvent)
                            try {
                                bot.sendMessage(ServingMessage.getTimedMessage(linkedQuest.user, goodText))
                            } catch (e: TelegramApiRequestException) {
                                if (e.errorCode != 403) {
                                    e.printStackTrace()
                                }
                            } catch (e: TelegramApiException) {
                                e.printStackTrace()
                            }

                        }
                        badText += "\n\nОчень жаль! Твоя награда за задание будет уменьшена."
                        event.win = false
                        event.step.doFinal(event)
                        questEventRepository.save(event)
                        quest.eventTime = quest.questEnum.getNextEventTime(quest)
                        questRepository.save(quest)
                        send(ServingMessage.getTimedMessage(usr, badText))
                    } else if (event == null && quest.eventTime!!.before(Date())) {
                        if (quest.startTime.before(DateUtils.addDays(Date(), -2))) {
                            quest.returnTime = Date()
                            val reward = evalReward(quest)
                            quest.goldEarned = reward
                            questRepository.save(quest)
                            usr.location = Location.TAVERN
                            usr.gold = usr.gold + reward
                            userRepository.save(usr)
                            send(ServingMessage.getTimedMessage(usr, "Ты пробыл на задании довольно долго, пора возвращаться. Ты заработав {0} {1}".format(reward, Emoji.GOLD)))
                        } else {
                            val iQuestEvent = quest.questEnum.iQuest.randomEvent
                            event = QuestEvent()
                            event.eventTime = quest.eventTime
                            event.quest = quest
                            event.step = iQuestEvent.init
                            iQuestEvent.init(quest)
                            event.iQuestEvent = iQuestEvent
                            questEventRepository.save(event)
                            iQuestEvent.init.doWork(event)
                            send(ServingMessage.getTimedMessage(usr, iQuestEvent.init.getText(quest)))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun send(message: SendMessage) {
        try {
            bot.sendMessage(message)
        } catch (e: TelegramApiRequestException) {
            if (e.errorCode != 403) {
                e.printStackTrace()
            }
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}