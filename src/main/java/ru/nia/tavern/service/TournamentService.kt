package ru.nia.tavern.service

import org.nia.strings.Emoji
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.nia.tavern.model.Tournament
import ru.nia.tavern.model.TournamentBet
import ru.nia.tavern.model.TournamentUsers
import ru.nia.tavern.model.User
import ru.nia.tavern.model.repository.TournamentRepository
import ru.nia.tavern.model.types.TournamentState
import java.util.*

/**
 * @author ianazarov
 * (c) RGS
 * created 2018-11-07
 */
class TournamentService {

    private val REGISTRATION_TIME = 15
    private var tournamentRepository: TournamentRepository
    private var userServingService: UserServingService

    constructor(tournamentRepository: TournamentRepository, userServingService: UserServingService) {
        this.tournamentRepository = tournamentRepository
        this.userServingService = userServingService
    }

    fun processTournament(tournament: Tournament): List<String> {
        val res = ArrayList<String>()
        if (tournament.isAnnounced) {
            tournament.state = TournamentState.REGISTRATION
            res.add("""
                Регистрация на турнир ${tournament.type} открыта на ${REGISTRATION_TIME} минут! Приходи в @drinkstardust и жми /register срочно!
                Максимальное число участников - ${tournament.maxUsers}. Торопитесь принять участие!

                Кроме того, пока идет регистрация вы можете поставить ставку на зарегистрировавшихся участников командой /bet
            """.trimIndent())
        } else if (tournament.isRegistration) {
            val pair = TournamentUsers.getTwoUsers(tournament)
            if (pair == null || pair.right == null) {
                tournament.state = TournamentState.FINISHED
                res.add("Турнира не будет. Не набралось и двух участников")
            } else {
                tournament.state = TournamentState.PROGRESS
                res.add("""
                    ${Emoji.DRINKS.toString()}Турнир ${tournament.type} начинается!
                    Главный приз - почет и уважение!
                    ${Emoji.DRINK}

                    Полный список участников:
                    ${TournamentUsers.getAllString(tournament, tournament.round + 1)}
                    Первое состязание состоится через 1 минуту, всем занять свои места, МЫ НАЧИНАЕМ!
                """.trimIndent())
            }
        } else if (tournament.isInProgress) {
            val pair = TournamentUsers.getTwoUsers(tournament)
            if (pair == null) {
                tournament.state = TournamentState.FINISHED
                res.add("Увы, но на финал турнира не нашлось бойцов. Турнир окончен до выявления победителя.")
            } else {
                val left = pair.left
                val right = pair.right
                if (right == null) {
                    tournament.winner = left.user
                    tournament.state = TournamentState.FINISHED
                    try {
                        CWTavernBot.INSTANCE.sendMessage(SendMessageService.getTournamentMessage("""
                            Итак, сегодняший турнир окончен, победитель определен! Им стал ${left.user}. Дружище, ты лучше всех!
                        """.trimIndent()
                        ))
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }

                    res.add(TournamentBet.evalTournamentResults(left))
                } else if (left.round < right.round) {
                    left.incRound()
                    left.user.incFightClubWins()
                    res.add("${left.user}, тебе повезло, соперника не нашлось, и ты автоматически проходишь в следующий этап.")
                } else {
                    if (tournament.round < left.round) {
                        tournament.round++
                        val phase = tournament.maxUsers / java.lang.Double.valueOf(Math.pow(2.0, tournament.round.toDouble())).toInt()
                        var roundStr = "Начинается следующий этап!\n"
                        when (phase) {
                            1 -> roundStr = "Начинается финал!\n"
                            2 -> roundStr = "Начинается полуфинал!\n"
                            4 -> roundStr = "Начинается четвертьфинал!\n"
                            8 -> roundStr = "Начинается 1/8 финала!\n"
                            16 -> roundStr = "Начинается 1/16 финала!\n"
                            32 -> roundStr = "Начинается 1/32 финала!\n"
                        }
                        res.add(roundStr + TournamentUsers.getAllString(tournament, tournament.round))
                    }
                    if (left.inFight) {
                        var fightResult: String
                        if (left.score == 0 && right.score == 0) {
                            left.lose = true
                            right.lose = true
                            left.inFight = false
                            right.inFight = false
                            fightResult = "Оба участника пропустили состязание и были дисквалифицированы! " + left.user + ", " + right.user + " в следующий раз будете " +
                                    "смелее!\n"
                        } else if (left.score == 0 || right.score == 0) {
                            var winner = left
                            var loser: TournamentUsers = right
                            if (left.score == 0) {
                                winner = right
                                loser = left
                            }
                            loser.lose = true
                            loser.inFight = false
                            winner.incRound()
                            winner.user.incFightClubWins()
                            winner.inFight = false
                            winner.score = 0
                            fightResult = "Участник " + loser.user + " трус, он не явился на состязание! Его соперник " + winner.user + " автоматически проходит в " +
                                    "следующий этап!\n"
                        } else {

                            val leftScore = left.getFinalResult(right)
                            val rightScore = right.getFinalResult(left)
                            if (leftScore != rightScore) {
                                lateinit var winner: TournamentUsers
                                lateinit var loser: TournamentUsers
                                if (leftScore < rightScore) {
                                    winner = right
                                    loser = left
                                } else if (leftScore > rightScore) {
                                    winner = left
                                    loser = right
                                }
                                loser.inFight = false
                                loser.lose = true
                                fightResult = tournament.type.getWinPhrase(winner, loser) + "\n"
                                winner.incRound()
                                winner.user.incFightClubWins()
                                winner.inFight = false
                                winner.score = 0
                            } else {
                                left.score = 0
                                left.inFight = false
                                right.score = 0
                                right.inFight = false
                                fightResult = tournament.type.getTie(left, right) + "\n"
                            }
                        }
                        OficiantThread.INSTANCE.setTournamentPhase(true)
                        fightResult += "Следующее состязание вот-вот начнется!"
                        res.add(fightResult)
                        User.flushVotes()
                    } else {
                        left.inFight = true
                        right.inFight = true
                        res.add("${left.user} - твой выход!\n\n" + userServingService.publicFightClubStats(left.user))
                        res.add("${right.user} - твой выход!\n\n" + userServingService.publicFightClubStats(right.user))
                        res.add(tournament.type!!.rule + "\n\nРАУНД НАЧИНАЕТСЯ!")
                        tournament.type.remindUser(left.user)
                        tournament.type.remindUser(right.user)
                    }
                }
            }
        }
        tournamentRepository.save(tournament)
        return res
    }
}