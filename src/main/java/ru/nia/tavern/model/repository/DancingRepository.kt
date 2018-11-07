package ru.nia.tavern.model.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.nia.tavern.model.Dancing

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
@Repository
interface DancingRepository : JpaRepository<Dancing, Int> {
}