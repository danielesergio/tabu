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