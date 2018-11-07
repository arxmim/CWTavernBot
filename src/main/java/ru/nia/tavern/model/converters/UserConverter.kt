package ru.nia.tavern.model.converters

import org.telegram.telegrambots.api.objects.Message
import ru.nia.tavern.model.User
import ru.nia.tavern.model.repository.UserRepository
import ru.nia.tavern.model.types.Location

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
class UserConverter {
    private var userRepository: UserRepository

    constructor(userRepository: UserRepository) {
        this.userRepository = userRepository
    }


    fun getFromMessage(message: Message): User {
        return getFromMessage(message.from)
    }

    fun getFromMessage(user: org.telegram.telegrambots.api.objects.User): User {
        val userID = user.id!!
        val result = userRepository
                .findById(userID)
                .map {
                    it.nick = user.userName
                    it.name = user.firstName
                    it
                }
                .orElseGet {
                    val res = User()
                    res.nick = user.userName
                    res.name = user.firstName
                    res.userID = userID
                    res.alkoCount = 0
                    res.isBarmen = false
                    res.drinkedTotal = 0
                    res.isAdmin = false
                    res.gold = 30
                    res.fightTime = null
                    res.curseTime = null
                    res.location = Location.TAVERN
                    res.foodCount = 0
                    res.eatTotal = 0
                    res.fightClubWins = 0
                    res.brewCount = 0
                    res.drinkedWeek = 0
                    res.voteFor = null
                    res
                }
        userRepository.save(result)
        return result
    }
}