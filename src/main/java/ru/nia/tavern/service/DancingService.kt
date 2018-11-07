package ru.nia.tavern.service

import org.apache.commons.lang3.time.DateUtils
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Dancing
import ru.nia.tavern.model.repository.DancingRepository
import ru.nia.tavern.model.repository.UserRepository
import java.util.*

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
class DancingService {
    private var userRepository: UserRepository
    private var dancingRepository: DancingRepository

    constructor(userRepository: UserRepository, dancingRepository: DancingRepository) {
        this.userRepository = userRepository
        this.dancingRepository = dancingRepository
    }

    fun doDance(dancing: Dancing) {

        if (dancing.nextStepTime == null) {
            dancing.nextStepTime = DateUtils.addSeconds(Date(), dancing.currentStep.stepDuration)
            dancingRepository.save(dancing)
            val message = dancing.currentStep.getInitialSendMessage(dancing)
            try {
                CWTavernBot.INSTANCE.sendMessage(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        } else if (dancing.nextStepTime!!.before(Date())) {
            if (dancing.nextAction != null) {
                dancing.completed = false
                dancingRepository.save(dancing)
                dancing.firstDancer.danceWithUser = null
                dancing.secondDancer.danceWithUser = null
                userRepository.save(dancing.firstDancer)
                userRepository.save(dancing.secondDancer)
                val message = dancing.currentStep.getTimedFailMessage(dancing)
                try {
                    CWTavernBot.INSTANCE.sendMessage(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            } else if (dancing.currentStep.hasNextStep(dancing)) {
                dancing.lastDanceAction = null
                dancing.lastActionFromFirst = null
                dancing.currentStep = dancing.currentStep.nextStep(dancing)
                dancing.nextStepTime = DateUtils.addSeconds(Date(), dancing.currentStep.stepDuration)
                dancingRepository.save(dancing)
                val message = dancing.currentStep.getInitialSendMessage(dancing)
                try {
                    CWTavernBot.INSTANCE.sendMessage(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            } else {
                dancing.completed = true
                dancingRepository.save(dancing)
                dancing.firstDancer.danceWithUser = null
                dancing.secondDancer.danceWithUser = null
                userRepository.save(dancing.firstDancer)
                userRepository.save(dancing.secondDancer)
                val message = dancing.currentStep.getSuccessMessage(dancing)
                try {
                    CWTavernBot.INSTANCE.sendMessage(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            }
        }
    }
}