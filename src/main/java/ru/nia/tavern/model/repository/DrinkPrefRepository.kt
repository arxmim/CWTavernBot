package ru.nia.tavern.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.nia.tavern.model.DrinkPref
import ru.nia.tavern.model.User
import ru.nia.tavern.model.types.DrinkType

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Repository
interface DrinkPrefRepository : JpaRepository<DrinkPref, Int> {
    fun findByUser(user: User): List<DrinkPref>
    fun findByUserAndDrinkType(user: User, drinkType: DrinkType): DrinkPref?
}