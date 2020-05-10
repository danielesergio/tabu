/*
 * Tabu is a card game where you must guess the main word without using the tabu words
 *
 * Copyright (c) 2020 Daniele Sergio
 *
 * This file is part of tabu.
 *
 * tabu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tabu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.danielesergio.tabu.usecases.entites

import com.danielesergio.tabu.entities.GameStatus
import com.danielesergio.tabu.entities.Round
import com.danielesergio.tabu.entities.Rule
import com.danielesergio.tabu.entities.Team
import com.danielesergio.tabu.usecases.GameContext
import com.danielesergio.tabu.usecases.entites.RuleImpl.Factory.next
import com.danielesergio.tabu.usecases.gameconfiguration.RuleData
import com.danielesergio.tabu.usecases.provider.CountDownTimer
import com.danielesergio.tabu.usecases.provider.OnFinish
import java.util.*

data class RuleImpl constructor(
    val pointLimit:Int,
    val pass:Int,
    val resetEveryRound:Boolean,
    val duration:Long,
    private val turns:Queue<TeamImpl>): Rule {

    object Factory{
        fun newInstance(ruleData: RuleData, gameStatus: GameStatus.Ongoing? = null):Rule{
            return RuleImpl(
                pointLimit = ruleData.pointLimit,
                pass = ruleData.pass,
                resetEveryRound = ruleData.resetEveryRound,
                duration = ruleData.duration,
                turns = LinkedList(TeamImpl.values().asList()).apply {
                    repeat(gameStatus?.rounds?.size?:0) {
                        this.next()
                    }
                }
            )
        }

        internal fun Queue<TeamImpl>.next():Team{
            return remove().also { team ->
                add(team)
            }
        }
    }

    override fun onUseTabuWord(round: Round): Round {
        return (round as RoundImpl).addPoint(- 1)
    }

    override fun onPass(round: Round): Round {
        return (round as RoundImpl).addPassUsed()
    }

    override fun onGuessWord(round: Round): Round {
        return (round as RoundImpl).addPoint(1)

    }

    override fun onRoundEnd(gameStatus: GameStatus.Ongoing, round: Round): GameStatus {
        val gmTemp = gameStatus.copy(gameStatus.rounds.toMutableList().apply { add(round) })
        return when{
            isGameEnd(gmTemp)-> GameStatus.Terminate(
                gmTemp.rounds
            )
            else -> gmTemp
        }
    }

    override fun canPass(round: Round, gameStatus: GameStatus.Ongoing): Boolean {
        return resetEveryRound && round.passUsed < pass ||
                gameStatus.aggregatedTeamRounds(round.team).passUsed < pass
    }

    override fun nextRound(gameStatus: GameStatus.Ongoing, onRoundEnd: OnFinish): Round {
        return turns.next().run {
            RoundImpl(
                countDownTimer = GameContext.countDownTimerProvider.get(duration, onRoundEnd),
                team = this,
                point = 0,
                passUsed = 0)
        }
    }


    private fun isGameEnd(gameStatus: GameStatus):Boolean{
        val ranking = gameStatus.ranking()
        return gameStatus.allTeamPlayedSameRounds() &&
                ranking[0].point > ranking[1].point &&
                ranking[0].point >= pointLimit
    }
}