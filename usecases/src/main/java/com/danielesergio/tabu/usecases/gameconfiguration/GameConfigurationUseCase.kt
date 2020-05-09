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

package com.danielesergio.tabu.usecases.gameconfiguration

import com.danielesergio.tabu.usecases.GameContext
import com.danielesergio.tabu.usecases.toRule

class GameConfigurationUseCase(
    private val presenter: GameConfigurationPresenter
): GameConfigurationController {
    override fun chooseRule(ruleData: RuleData) {
        GameContext.rule = ruleData.toRule()
        presenter.gameReady()
    }
}