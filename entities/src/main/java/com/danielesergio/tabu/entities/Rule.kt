package com.danielesergio.tabu.entities

interface Rule {
    fun onUseTabuWord(round: Round):Round
    fun onPass(round: Round):Round
    fun onGuessWord(round: Round):Round
    fun onRoundEnd(gameStatus: GameStatus.Ongoing, round: Round):GameStatus
}