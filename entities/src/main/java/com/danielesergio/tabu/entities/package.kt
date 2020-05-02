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

