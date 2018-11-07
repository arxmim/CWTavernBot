package ru.nia.tavern.model

import ru.nia.tavern.quests.QuestsEnum
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * @author IANazarov
 */
@Entity
@Table(name = "cwt_Quest")
class Quest {
    @Id
    @Column
    @GeneratedValue
    var publicID: Int? = null
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    var user: User? = null
    @Column(name = "questName")
    @Enumerated(EnumType.STRING)
    lateinit var questEnum: QuestsEnum
    @Column(nullable = false)
    lateinit var startTime: Date
    @Column
    var eventTime: Date? = null
    @Column
    var returnTime: Date? = null
    @Column(columnDefinition = "INT DEFAULT 0")
    var goldEarned = 0
}
