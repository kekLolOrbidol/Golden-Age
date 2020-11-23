package com.moisha.snek.graphics

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.moisha.snek.game.Game
import com.moisha.snek.graphics.shapes.Square
import com.moisha.snek.graphics.shapes.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(type: Int, getDrawData: () -> Array<IntArray>) : GLSurfaceView.Renderer {

    companion object {
        const val TYPE_GAME_RENDER: Int = 1
        const val TYPE_EDITOR_RENDER: Int = 2
        const val TYPE_PREVIEW: Int = 3
    }

    private lateinit var square: Square
    private lateinit var triangle: Triangle
    private val getDrawData: () -> Array<IntArray> = getDrawData

    //identifier to know is it game or editor - what menu to draw
    // 1 - game, 2 - editor
    private var renderType: Int = type

    //field size in drawable units
    private var fieldX: Int = 1
    private var fieldY: Int = 1

    //drawable area size in pixels
    private var sourceX: Int = 0
    private var sourceY: Int = 0

    //single field unit size in OpenGL coords
    private var partSizeX: Float = 0.0f
    private var partSizeY: Float = 0.0f

    //menu block height in OpenGL coords and pixels
    private var menuSizeY: Float = 0.0f
    private var menuSizeYpt: Int = 0

    //single field unit size in pixels
    private var partPt: Int = 0

    //field offsets by x on screen in OpenGL coords and pixels
    private var xOffsetPt: Int = 0
    private var xOffset: Float = 0.0f

    //play button sizes in OpenGL coords
    private var pButtonSizeX: Float = 0.0f
    private var pButtonSizeY: Float = 0.0f

    private var squares: List<Array<FloatArray>> = listOf()

    private var menu: Array<List<FloatArray>> = arrayOf()

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }

        square = Square(mProgram)
        triangle = Triangle(mProgram)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        sourceX = width
        sourceY = height

        calcTransform()
        setMenu()
    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //redraw menu
        redrawMenu()

        //get drawing data
        redrawField(
            getDrawData()
        )

        //draw all pending squares
        for (i in squares) {
            square.draw(
                i[0], //coords
                i[1] //color
            )
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun redrawMenu() {

        //draw all sent squares
        for (i in 0..menu[0].lastIndex) {
            square.draw(menu[0].get(i), menu[1].get(i))
        }

        //draw all sent triangles
        for (i in 0..menu[2].lastIndex) {
            triangle.draw(menu[2].get(i), menu[3].get(i))
        }
    }

    fun redrawField(field: Array<IntArray>) {

        //if field size was changed - recalculate sizes and offsets in OpenGL coords
        if (field.size != fieldX || field[0].size != fieldY) {
            this.fieldX = field.size
            this.fieldY = field[0].size
            calcTransform()
        }

        //list for squares and their colors in OpenGL coords
        val squares: MutableList<Array<FloatArray>> = mutableListOf()

        //field background - white area of field size
        squares.add(
            arrayOf(
                floatArrayOf(
                    -1.0f + xOffset, 1.0f - partSizeY * fieldY,
                    -1.0f + xOffset, 1.0f,
                    1.0f - xOffset, 1.0f,
                    1.0f - xOffset, 1.0f - partSizeY * fieldY
                ),
                floatArrayOf(
                    1.0f, 1.0f, 1.0f, 1.0f
                )
            )
        )

        //field reading loop for adding data to drawing arrays
        for (i in 0..field.lastIndex) {
            for (j in 0..field[i].lastIndex) {
                if (field[i][j] == Game.EMPTY_UNIT) { //nothing to draw if unit is empty - continue
                    continue
                }
                val square =
                    Array(
                        2,
                        { floatArrayOf() }
                    )
                square[0] =
                    floatArrayOf(
                        xOffset + -1.0f + partSizeX * i, 1.0f - partSizeY * j - partSizeY,
                        xOffset + -1.0f + partSizeX * i, 1.0f - partSizeY * j,
                        xOffset + -1.0f + partSizeX * i + partSizeX, 1.0f - partSizeY * j,
                        xOffset + -1.0f + partSizeX * i + partSizeX, 1.0f - partSizeY * j - partSizeY
                    )
                when (field[i][j]) {
                    Game.DIRECTION -> {
                        square[1] =
                            floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
                    }
                    Game.BARRIER -> {
                        square[1] =
                            floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
                    }
                    Game.MEAL -> {
                        square[1] =
                            floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f)
                    }
                    else -> { //Snek
                        square[1] =
                            floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)
                    }
                }
                squares.add(square)
            }
        }

        this.squares = squares.toList()
    }

    //determine on what unit was clicked
    fun traceClick(xTouch: Int, yTouch: Int): IntArray {
        val coords = IntArray(2, { -1 })

        if (yTouch > sourceY - menuSizeYpt) {
            if (xTouch < sourceX / 6) {
                coords[1] = 1
            } else if (xTouch < sourceX / 6 * 2) {
                coords[1] = 2
            } else if (xTouch < sourceX / 6 * 3) {
                coords[1] = 3
            } else if (xTouch < sourceX / 6 * 4) {
                coords[1] = 4
            } else if (xTouch < sourceX / 6 * 5) {
                coords[1] = 5
            } else {
                coords[1] = 6
            }
        } else if (renderType == TYPE_EDITOR_RENDER) { //not needed to react if is not editor
            if (xTouch >= xOffsetPt && xTouch <= xOffsetPt + partPt * fieldX && yTouch <= partPt * fieldY) {
                coords[0] = (xTouch - xOffsetPt) / partPt
                if (coords[0] == fieldX) coords[0]-- //FB-03 fix
                coords[1] = yTouch / partPt
            }
        }

        return coords
    }

    private fun calcTransform() {
        menuSizeY = (2.0f / sourceY.toFloat()) * sourceX.toFloat() / 6.0f

        menuSizeYpt = (menuSizeY * sourceY.toFloat() / 2.0f).toInt()

        val freeSizeYpt: Int = sourceY - sourceX / 6

        //field unit sizes calculation
        if (freeSizeYpt / fieldY < sourceX / fieldX) {
            partSizeY = (2.0f - menuSizeY) / fieldY.toFloat()
            partSizeX = partSizeY / sourceX.toFloat() * sourceY.toFloat()
        } else {
            partSizeX = 2.0f / fieldX.toFloat()
            partSizeY = partSizeX / sourceY.toFloat() * sourceX.toFloat()
        }

        partPt = (partSizeY / 2 * sourceY).toInt()

        //play button size calculation
        if (freeSizeYpt < sourceX) {
            pButtonSizeY = (2.0f - menuSizeY) / 2
            pButtonSizeX = pButtonSizeY / sourceX.toFloat() * sourceY.toFloat()
        } else {
            pButtonSizeX = 1.0f
            pButtonSizeY = pButtonSizeX / sourceY.toFloat() * sourceX.toFloat()
        }

        //x margins to draw field centered horizontally
        xOffset = (2.0f - partSizeX * fieldX) / 2
        xOffsetPt = (sourceX - (partPt * fieldX)) / 2
    }

    //hardcoded menu views
    private fun setMenu() {

        val tr_coords: MutableList<FloatArray> = mutableListOf()
        val tr_colors: MutableList<FloatArray> = mutableListOf()
        val sq_coords: MutableList<FloatArray> = mutableListOf()
        val sq_colors: MutableList<FloatArray> = mutableListOf()

        val buttonSizeX: Float = 2.0f / 6.0f

        if (renderType == 1) { //game menu

            //menu background
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX, -1.0f,
                    -1.0f + buttonSizeX, -1.0f + menuSizeY,
                    1.0f - buttonSizeX, -1.0f + menuSizeY,
                    1.0f - buttonSizeX, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f)
            )

            //left
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 1.9f, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 1.1f, -1.0f + (menuSizeY / 2),
                    -1.0f + buttonSizeX * 1.9f, -1.0f
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //down
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 2, -1.0f + (menuSizeY * 0.9f),
                    -1.0f + buttonSizeX * 2.5f, -1.0f + (menuSizeY * 0.1f),
                    -1.0f + buttonSizeX * 3, -1.0f + (menuSizeY * 0.9f)
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //up
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 3, -1.0f + (menuSizeY * 0.1f),
                    -1.0f + buttonSizeX * 3.5f, -1.0f + (menuSizeY * 0.9f),
                    -1.0f + buttonSizeX * 4, -1.0f + (menuSizeY * 0.1f)
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //right
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 4.1f, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 4.9f, -1.0f + (menuSizeY / 2),
                    -1.0f + buttonSizeX * 4.1f, -1.0f
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

        } else if (renderType == 2) { //editor menu

            //adding Snek edit button
            sq_coords.add(
                floatArrayOf(
                    -1.0f, -1.0f,
                    -1.0f, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    0.0f, 0.0f, 1.0f, 1.0f
                )
            )

            //adding barrier edit button
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX, -1.0f,
                    -1.0f + buttonSizeX, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 2, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 2, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    0.0f, 0.0f, 0.0f, 1.0f
                )
            )

            //adding clear Snek button
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 2, -1.0f,
                    -1.0f + buttonSizeX * 2, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 3, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 3, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    0.0f, 0.0f, 1.0f, 1.0f
                )
            )
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 2, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 3, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 2.5f, -1.0f
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //adding clear barriers button
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 3, -1.0f,
                    -1.0f + buttonSizeX * 3, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 4, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 4, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    0.0f, 0.0f, 0.0f, 1.0f
                )
            )
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 3, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 4, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 3.5f, -1.0f
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //adding clear Snek button
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 4, -1.0f,
                    -1.0f + buttonSizeX * 4, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 5, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 5, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    1.0f, 0.0f, 1.0f, 1.0f
                )
            )
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 4, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 5, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 4.5f, -1.0f + (menuSizeY / 2)
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 4, -1.0f,
                    -1.0f + buttonSizeX * 5, -1.0f,
                    -1.0f + buttonSizeX * 4.5f, -1.0f + (menuSizeY / 2)
                )
            )
            tr_colors.add(
                floatArrayOf(
                    1.0f, 1.0f, 0.0f, 1.0f
                )
            )

            //adding save button
            sq_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 5, -1.0f,
                    -1.0f + buttonSizeX * 5, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 6, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 6, -1.0f
                )
            )
            sq_colors.add(
                floatArrayOf(
                    0.0f, 0.0f, 0.0f, 1.0f
                )
            )
            tr_coords.add(
                floatArrayOf(
                    -1.0f + buttonSizeX * 5.2f, -1.0f + menuSizeY,
                    -1.0f + buttonSizeX * 6, -1.0f + (menuSizeY / 2),
                    -1.0f + buttonSizeX * 5.2f, -1.0f
                )
            )
            tr_colors.add(
                floatArrayOf(
                    0.0f, 1.0f, 0.0f, 1.0f
                )
            )

        }

        //store calculated data for rendering
        menu = arrayOf(
            sq_coords.toList(),
            sq_colors.toList(),
            tr_coords.toList(),
            tr_colors.toList()
        )
    }

    private fun hasSquares(): Boolean {
        return squares.isNotEmpty()
    }

}