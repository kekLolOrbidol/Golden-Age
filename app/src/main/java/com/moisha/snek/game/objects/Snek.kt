package com.moisha.snek.game.objects

/**
 * Class for handling a snek as game object
 */

class Snek(startSnek: Collection<IntArray>, startDirection: Int) {

    // list of xy coordinates of snek location
    private val state: MutableList<IntArray> = startSnek.toMutableList()

    // actual direction of snek moving
    // 1 - right, 2 - down, 3 - left, 4 - up
    private var direction: Int = startDirection

    // sets new direction, if it is not opposite to actual and not same as actual
    fun setDirection(newDirection: Int) {
        if (allowedDirection(newDirection)) {
            this.direction = newDirection
        }
    }

    fun getDirection(): Int {
        return this.direction
    }

    fun allowedDirection(direction: Int): Boolean {
        return (this.direction - direction != 2 && this.direction - direction != -2)
    }

    // moves the snek to the next position, according to actual direction
    // returns true if move was successfull or false if game lost
    // checks meals, barriers and flat boundaries by functions provided as arguments
    fun moveByDirection(
        meal: (IntArray) -> Boolean,
        barCheck: (IntArray) -> Boolean,
        inRoom: (IntArray) -> IntArray
    ): Int {

        val newHead = inRoom(newHead()) // getting new sneks head position, keeping snek in flat

        for (i in state) {
            // if snek trying to go over itself - return -1
            if (i.contentEquals(newHead) && state.indexOf(i) > 0) {
                return -1
            }
        }

        // or if snek going on barrier
        if (!barCheck(newHead)) {
            return -1
        }

        // move whole snek or grow to the direction of moving, if meal was eaten
        state.add(newHead)
        if (!meal(newHead)) {
            state.removeAt(0)
            return 0 //not grown up, wasn't meal - return 0
        }

        return 1 // grown up to meal - return 1
    }

    private fun newHead(): IntArray {
        val head: IntArray = state.last().clone()

        when (this.direction) { // calculating new sneks head position
            1 -> head[0]++
            3 -> head[0]--
            2 -> head[1]++
            4 -> head[1]--
        }

        return head
    }

    fun onPoint(point: IntArray): Boolean {

        for (i in state) {
            if (i.contentEquals(point)) {
                return true
            }
        }

        return false
    }

    fun getSnek(): List<IntArray> {
        return state.toList()
    }
}