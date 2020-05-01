package com.danielesergio.tabu.entities

fun List<Round>.reduce():Round {
    data class RoundImpl constructor(
        override val point: Int,
        override val passUsed: Int,
        override val team: Team
    ) : Round

    return this.reduce { acc, ele ->
        if (acc.team == ele.team) {
            throw IllegalArgumentException("All rounds must have same team")
        }
        RoundImpl(acc.point + ele.point, acc.passUsed + ele.passUsed, ele.team)
    }
}

