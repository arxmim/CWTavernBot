package ru.nia.tavern.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.nia.tavern.model.User

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findTop1ByNick(nick: String): User
}