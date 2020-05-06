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

data class RuleImpl(
    val pointLimit:Int,
    val pass:Int,
    val resetEveryRound:Boolean): Rule {
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

    private fun isGameEnd(gameStatus: GameStatus):Boolean{
        val ranking = gameStatus.ranking()
        return gameStatus.allTeamPlayedSameRounds() &&
                ranking[0].point > ranking[1].point &&
                ranking[0].point >= pointLimit
    }
}