/*
 * Tabu is a card game where you must guess the main word without using the tabu words
 *
 * Copyright (c) 2020 Daniele Sergio
 *
 * This file is part of Tabu.
 *
 * Tabu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tabu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.danielesergio.tabu.entities

sealed class GameStatus {
    data class Ongoing(override val rounds:List<Round> = emptyList()):GameStatus()
    data class Terminate(override val rounds:List<Round>):GameStatus()
    data class Pause(val gameStatus: GameStatus):GameStatus(){
        override val rounds = gameStatus.rounds
    }

    abstract val rounds:List<Round>

    fun teamPoints(team:Team):Int{
        return rounds.filter { round -> round.team == team }.reduce().point
    }

    fun allTeamPlayedSameRounds():Boolean{
        return rounds.groupBy { r -> r.team }
            .map { it.value.size }
            .distinct()
            .size == 1
    }

    fun ranking():List<TeamScore>{
        return rounds.groupBy { r -> r.team }
            .map { it.value.reduce().asTeamScore() }
            .sortedBy { it.point }

    }

    private fun Round.asTeamScore():TeamScore{
        class TeamScoreImpl(override val team:Team, override val point:Int):TeamScore
        return TeamScoreImpl(team,point)
    }
}