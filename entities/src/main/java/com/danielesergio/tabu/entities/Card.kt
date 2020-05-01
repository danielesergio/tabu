package com.danielesergio.tabu.entities

interface Card {
    val word:String
    val tabuWords:Set<String>
}