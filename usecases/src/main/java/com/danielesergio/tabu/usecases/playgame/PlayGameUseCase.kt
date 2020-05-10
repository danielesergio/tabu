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

package com.danielesergio.tabu.usecases.playgame

import com.danielesergio.tabu.entities.Game
import com.danielesergio.tabu.entities.GameStatus
import com.danielesergio.tabu.usecases.GameContext
import com.danielesergio.tabu.usecases.entites.GameImpl
import com.danielesergio.tabu.usecases.provider.CardProvider
import com.danielesergio.tabu.usecases.provider.OnFinish
import com.danielesergio.tabu.usecases.toCardOutput
import com.danielesergio.tabu.usecases.toTeamStatus

class PlayGameUseCase(
    private val presenter: PlayGamePresenter,
    private val game: Game = GameImpl.newGame(GameContext.rule),
    private val cardProvider: CardProvider):PlayGame {

    private val onRoundFinish: OnFinish = {
        val gameStatus = game.rule.onRoundEnd(
            round = currentRound,
            gameStatus = game.gameStatus as GameStatus.Ongoing)
        val ranking = ranking(gameStatus)
        if(gameStatus is GameStatus.Terminate){
            presenter.gameEnd(ranking.toTypedArray())
        } else {
            presenter.roundEnd(currentTeam = ranking.first(), othersTeam = *ranking.subList(1, ranking.size).toTypedArray())
        }
    }

    private var currentRound = game.rule.nextRound(game.gameStatus as GameStatus.Ongoing, onRoundFinish)

    override fun guessWord() {
        currentRound = game.rule.onGuessWord(currentRound)
        presenter.updateScore(currentRound.toTeamStatus())
        nextCard()
    }

    override fun useTabuWord() {
        currentRound = game.rule.onUseTabuWord(currentRound)
        presenter.updateScore(currentRound.toTeamStatus())
        nextCard()
    }

    override fun pass() {
        currentRound = game.rule.onPass(currentRound)
        nextCard()
    }

    override fun pause() {
        currentRound.pause()
    }

    override fun start() {
        currentRound.start()
    }

    private fun ranking(gameStatus:GameStatus) = gameStatus
        .rounds
        .distinctBy { it.team }
        .map { gameStatus.aggregatedTeamRounds(it.team).run { TeamStatus(it.team.getName(), it.point, it.passUsed) } }
        .sortedByDescending { it.point }

    private fun nextCard(){
        cardProvider.nextCard().also {
            presenter.showCard(it.toCardOutput())
        }
    }
}

