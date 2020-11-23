package com.moisha.snek

import com.moisha.snek.database.model.Level
import com.moisha.snek.editor.EditorField
import com.moisha.snek.game.Game
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GameLogicUnitTest {
    private lateinit var game: Game
    private lateinit var level: Level
    private var score = 0

    @BeforeAll
    fun initLevel() {
        val editor: EditorField = EditorField(6, 6)
        editor.setSnek(0, 0)
        editor.setSnek(0, 1)
        editor.setSnek(0, 2)
        editor.setSnek(0, 3)
        editor.setSnek(0, 4)
        editor.setSnek(1, 4)
        editor.setSnek(2, 4)
        editor.setBarrier(1, 0)
        editor.setBarrier(2, 0)
        editor.setBarrier(3, 4)
        level = editor.getLevel(0)
    }

    @Test
    @Order(1)
    fun t1_initGame() { //should not pass
        game = Game(level, 0, 0)

        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 2, 3, 4, 5, 0),
                intArrayOf(-1, 0, 0, 0, 6, 0),
                intArrayOf(-1, 0, 0, 0, 7, 0),
                intArrayOf(0, 0, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0)
            ),
            game.getField(),
            "Normally drops on -2 instead of 0. In other case is error."
        )
    }

    @Test
    @Order(2)
    fun t2_moveOne() { //should not pass
        assertTrue(game.move(), "Move was successfull.")
        assertTrue(game.move(), "Move was successfull.")
        assertArrayEquals(
            arrayOf(
                intArrayOf(3, 4, 5, 6, 7, 2),
                intArrayOf(-1, 0, 0, 0, 0, 0),
                intArrayOf(-1, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0)
            ),
            game.getField(),
            "Normally drops on -2, 8 or 9 instead of 0. In other case is error."
        )
    }

    @Test
    @Order(3)
    fun t3_setDirection() { //should not pass
        game.setDirection(1)
        assertTrue(game.move(), "Move was successfull.")
        assertArrayEquals(
            arrayOf(
                intArrayOf(4, 5, 6, 7, 0, 3),
                intArrayOf(-1, 0, 0, 0, 0, 2),
                intArrayOf(-1, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0, 0)
            ),
            game.getField(),
            "Normally drops on -2, 8, 9 or 10 instead of 0. In other case is error."
        )
    }

    @Test
    @Order(4)
    fun t4_goOnBarrier() {
        assertTrue(game.move(), "Move was successfull.")
        assertTrue(game.move(), "Move was successfull.")
        game.setDirection(4)
        assertTrue(!game.move(), "Game ended on try to go over barrier.")
    }

    @Test
    @Order(5)
    fun t5_getResult() {
        val score: Int = game.getResult().score
        assertTrue(
            (score >= 0 && score <= 5),
            "Score is in correct range."
        )
    }

    @Test
    @Order(6)
    fun t6_snekOverSnek() {
        game = Game(level, 0, 0)
        game.setDirection(1)
        assertTrue(game.move(), "Move 1 was successfull.")
        game.setDirection(2)
        assertTrue(game.move(), "Move 2 was successfull.")
        game.setDirection(3)
        assertTrue(!game.move(), "Move over Snek was not successfull.")
    }

    @Test
    @Order(7)
    fun t7_getResult() {
        val score: Int = game.getResult().score
        assertTrue(
            (score >= 0 && score <= 2),
            "Score is in correct range."
        )
    }
}