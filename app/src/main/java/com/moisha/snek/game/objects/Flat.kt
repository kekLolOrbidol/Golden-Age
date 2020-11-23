package com.moisha.snek.game.objects

import kotlin.random.Random

/**
 * Class for providing flat data in actual game.
 */

class Flat(sizeX: Int, sizeY: Int) {

    // flat coordinate limits
    private val xRange: IntRange = IntRange(0, sizeX - 1)
    private val yRange: IntRange = IntRange(0, sizeY - 1)

    val keepInFlat = fun(coord: IntArray): IntArray {
        if (coord[0] > xRange.last) return intArrayOf(xRange.first, coord[1])
        if (coord[0] < xRange.first) return intArrayOf(xRange.last, coord[1])
        if (coord[1] > yRange.last) return intArrayOf(coord[0], yRange.first)
        if (coord[1] < yRange.first) return intArrayOf(coord[0], yRange.last)
        return coord // if nothing happened earlier - return as it is
    }

    // returns random point in flat boundaries
    val randomPoint = fun(): IntArray {
        return intArrayOf(Random.nextInt(xRange.first, xRange.last), Random.nextInt(yRange.first, yRange.last))
    }

}