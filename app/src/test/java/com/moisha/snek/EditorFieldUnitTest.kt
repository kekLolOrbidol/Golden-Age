package com.moisha.snek

import com.moisha.snek.database.model.Level
import com.moisha.snek.editor.EditorField
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EditorFieldUnitTest {
    private val editor: EditorField = EditorField(5, 3)

    @Test
    @Order(1)
    fun t1_SetBarriers() {
        editor.setBarrier(1, 2)
        editor.setBarrier(2, 2)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0),
                intArrayOf(0, 0, -1),
                intArrayOf(0, 0, -1),
                intArrayOf(0, 0, 0),
                intArrayOf(0, 0, 0)
            ),
            editor.getField(),
            "Snek and barriers in field"
        )
    }

    @Test
    @Order(2)
    fun t2_SetSnek() {
        editor.setSnek(1, 1)
        editor.setSnek(2, 1)
        editor.setSnek(3, 1)
        editor.setSnek(3, 0)
        editor.setSnek(3, 2)
        editor.setSnek(0, 2)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0),
                intArrayOf(0, 1, -1),
                intArrayOf(0, 2, -1),
                intArrayOf(4, 3, 5),
                intArrayOf(0, 0, 0)
            ),
            editor.getField(),
            "Snek and barriers in field"
        )
        assertEquals(5, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(3)
    fun t3_ChangeToSameSize() { //fixed bug FB-02
        editor.changeSize(5, 3)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0),
                intArrayOf(0, 1, -1),
                intArrayOf(0, 2, -1),
                intArrayOf(4, 3, 5),
                intArrayOf(0, 0, 0)
            ),
            editor.getField(),
            "Snek and barriers in field"
        )
        assertEquals(5, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(4)
    fun t4_ChangeSize() {
        editor.changeSize(5, 5)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 1, -1, 0, 0),
                intArrayOf(0, 2, -1, 0, 0),
                intArrayOf(4, 3, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After changing size"
        )
        assertEquals(4, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(5)
    fun t5_ClearBarriers() {
        editor.clearBarriers()
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, 0, 0),
                intArrayOf(4, 3, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After clearing barriers"
        )
    }

    @Test
    @Order(6)
    fun t6_SetUnreachableSnekPos() {
        editor.setSnek(0, 4)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, 0, 0),
                intArrayOf(4, 3, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After trying to set Snek to unreachable position"
        )
    }

    @Test
    @Order(7)
    fun t7_SetBarrierOnSnek() {
        editor.setBarrier(1, 1)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, 0, 0),
                intArrayOf(4, 3, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After trying to set barrier on Snek"
        )
    }

    @Test
    @Order(8)
    fun t8_SetAndRemoveBarriers() {
        editor.setBarrier(0, 3)
        editor.setBarrier(1, 3) // set barrier on (1;3)
        editor.setBarrier(1, 3) // remove barrier on (1;3)
        editor.setBarrier(2, 3)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, -1, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, -1, 0),
                intArrayOf(4, 3, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After setting and removing barriers"
        )
    }

    @Test
    @Order(9)
    fun t9_SetSnekOnBarrier() {
        editor.setSnek(3, 4)
        editor.setSnek(3, 3)
        editor.setSnek(2, 3)
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, -1, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, -1, 0),
                intArrayOf(4, 3, 0, 6, 5),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After trying to set Snek over barrier"
        )
        assertEquals(6, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(10)
    fun t10_RemoveSnekPart() {
        editor.setSnek(3, 4) // trying to remove NOT end of Snek
        editor.setSnek(3, 3) // removing end
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, -1, 0),
                intArrayOf(0, 1, 0, 0, 0),
                intArrayOf(0, 2, 0, -1, 0),
                intArrayOf(4, 3, 0, 0, 5),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After trying to set Snek over barrier"
        )
        assertEquals(5, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(11)
    fun t11_ClearSnek() {
        editor.clearSnek()
        assertArrayEquals(
            arrayOf(
                intArrayOf(0, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor.getField(),
            "After removing Snek"
        )
        assertEquals(0, editor.getSnekSize(), "Snek size is")
    }

    @Test
    @Order(12)
    fun t12_GetLevelObject() {
        editor.setSnek(0, 4)
        editor.setSnek(0, 0)
        editor.setSnek(0, 1)
        editor.setSnek(1, 1)
        editor.setSnek(1, 2)
        editor.setSnek(1, 3)
        editor.setSnek(1, 4)
        editor.setSnek(1, 0)
        editor.setSnek(2, 0)
        val level: Level = editor.getLevel(1)
        assertArrayEquals(intArrayOf(5, 5), level.size, "Level size is")
        for (i: Int in 0..1) {
            assertArrayEquals(
                listOf(
                    intArrayOf(0, 3),
                    intArrayOf(2, 3)
                ).get(i), level.barriers.get(i), "Barrier " + i.toString() + " is"
            )
        }
        assertEquals(8, level.snek.size)
        for (i: Int in 0..6) {
            assertArrayEquals(
                listOf(
                    intArrayOf(0, 0),
                    intArrayOf(0, 1),
                    intArrayOf(1, 1),
                    intArrayOf(1, 2),
                    intArrayOf(1, 3),
                    intArrayOf(1, 4),
                    intArrayOf(1, 0),
                    intArrayOf(2, 0)
                ).reversed().get(i), level.snek.get(i), "Sneks " + i.toString() + " element is"
            )
        }
        assertEquals(4, level.direction, "Sneks direction is")
    }

    @Test
    @Order(13)
    fun t13_UnpackLevel() {
        val level: Level = editor.getLevel(1)
        val editor2: EditorField = EditorField(level)
        assertArrayEquals(
            arrayOf(
                intArrayOf(2, 3, 0, -1, 1),
                intArrayOf(8, 4, 5, 6, 7),
                intArrayOf(9, 0, 0, -1, 0),
                intArrayOf(0, 0, 0, 0, 0),
                intArrayOf(0, 0, 0, 0, 0)
            ),
            editor2.getField(),
            "Snek after unpacking"
        )
    }

}