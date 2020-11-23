package com.moisha.snek.game.objects

/**
 * Class for providing maze data for actual game.
 */

class Maze(barriers: Collection<IntArray>) {

    private val barriers: List<IntArray> = barriers.toList()

    // check if exists barrier on provided place
    val checkBarrier = fun(coord: IntArray): Boolean {

        for (i in barriers) {
            if (i.contentEquals(coord)) {
                return false //if on barrier
            }
        }

        return true //if not on barrier
    }

    fun getBarriers(): List<IntArray> {
        return barriers
    }


}