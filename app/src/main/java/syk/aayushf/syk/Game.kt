package syk.aayushf.syk

import java.util.*

/**
 * Created by aayushf on 12/9/17.
 */
data class Game(var id:String = UUID.randomUUID().toString().substring(0,7), var players:List<Player> = listOf()) {
    constructor(initPlayer: Player) : this() {
        Game(players = listOf(initPlayer))

    }

}