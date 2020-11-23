package com.moisha.snek.editor

import com.moisha.snek.database.model.Level

/**
 * Class for handling editor game field
 * Represented as 2-dimensional integer array
 * Values meaning:
 *      0 - empty field
 *      -1 - barrier
 *      1 - direction
 *      >1 - Snek
 */

class EditorField(x: Int, y: Int) : EditorBase(x, y) {

    companion object {
        const val ACTION_SET_SNEK = 1
        const val ACTION_SET_BARRIER = 2
    }
    //actually used reaction on field unit touch
    /**
     * 1 - set Snek
     * 2 - set barrier
     * else - nothing
     */
    private var action: Int = 0

    constructor(level: Level) : this(level.size[0], level.size[1]) {

        //unpacking barriers
        for (i in level.barriers) {
            setBarrier(i[0], i[1])
        }

        //setting direction and snek
        val head: IntArray = level.snek.last()
        when (level.direction) {
            2 -> if (head[1] == y - 1) field[head[0]][0] = 1 else field[head[0]][head[1] + 1] = 1
            1 -> if (head[0] == x - 1) field[0][head[1]] = 1 else field[head[0] + 1][head[1]] = 1
            4 -> if (head[1] == 0) field[head[0]][y - 1] = 1 else field[head[0]][head[1] - 1] = 1
            3 -> if (head[0] == 0) field[x - 1][head[1]] = 1 else field[head[0] - 1][head[1]] = 1
        }
        snekSize = 1

        for (i in level.snek.reversed()) {
            snekSize++
            field[i[0]][i[1]] = snekSize
        }

        //setting level name
        levelName = level.name

    }

    fun getField(): Array<IntArray> {
        return field
    }

    fun getX(): Int {
        return this.x
    }

    fun getY(): Int {
        return this.y
    }

    fun setAction(action: Int) {
        this.action = action
    }

    fun react(coords: IntArray) {
        if (action == 1) {
            setSnek(coords[0], coords[1])
        } else if (action == 2) {
            setBarrier(coords[0], coords[1])
        }
    }

    fun setSnek(x: Int, y: Int): Boolean {
        //if it's the end of Sneks tail - remove it and return true
        if (field[x][y] == snekSize && snekSize != 0) {
            snekSize--
            field[x][y] = 0
            return true
        }

        //if it's behind the end of Sneks tail and coords is free - grow to coords and return true
        if (field[x][y] == 0 && (
                    field[if (x + 1 >= this.x) 0 else x + 1][y] == snekSize ||
                            field[if (x - 1 < 0) this.x - 1 else x - 1][y] == snekSize ||
                            field[x][if (y + 1 >= this.y) 0 else y + 1] == snekSize ||
                            field[x][if (y - 1 < 0) this.y - 1 else y - 1] == snekSize
                    )
        ) {
            snekSize++
            field[x][y] = snekSize
            return true
        }

        //if nothing happened - return false
        return false
    }

    fun setBarrier(x: Int, y: Int): Boolean {
        //change state, if empty or barrier
        when (field[x][y]) {
            -1 -> {
                field[x][y] = 0
            }
            0 -> {
                field[x][y] = -1
            }
            else -> {
                //if nothing happened with field
                return false
            }
        }
        //if not returned false - something happened, return true
        return true
    }

    fun clearSnek(): Boolean {
        //if no Snek in field - return false
        if (snekSize == 0) return false

        //else clear Snek and return true
        for (i in 0..x - 1) {
            for (j in 0..y - 1) {
                if (field[i][j] > 0) {
                    field[i][j] = 0
                    snekSize--
                }
            }
        }

        return true
    }

    fun clearBarriers() {
        for (i in 0..x - 1) {
            for (j in 0..y - 1)
                if (field[i][j] == -1) field[i][j] = 0
        }
    }

    fun changeSize(x: Int, y: Int) {
        //making new field of provided size
        var newField: Array<IntArray> = Array(x, { IntArray(y) })

        //copying barriers and Snek to new field
        newField = copySnek(copyBar(newField))

        //setting new size values
        this.x = x
        this.y = y

        //setting new field
        field = newField
    }

    fun getLevel(uId: Int): Level {
        val size = intArrayOf(this.x, this.y)
        val barriers = barList()
        val snek = readSnek()
        val direction = readDirection(snek)

        //subList works from inclusive to EXCLUSIVE!!! subList length is snek length - 1
        return Level(size, barriers, snek.subList(0, snek.lastIndex), direction, uId, this.levelName)
    }

    fun getSnekSize(): Int {
        return snekSize
    }
}