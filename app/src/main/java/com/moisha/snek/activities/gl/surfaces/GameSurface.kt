package com.moisha.snek.activities.gl.surfaces

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.moisha.snek.activities.gl.GameActivity
import com.moisha.snek.graphics.GLRenderer

class GameSurface(context: Context) : GLSurfaceView(context) {

    private val gameActivity: GameActivity = context as GameActivity
    private val mRenderer: GLRenderer

    init {

        setEGLContextClientVersion(2) //OpenGL ES 2.0

        mRenderer = GLRenderer(GLRenderer.TYPE_GAME_RENDER, gameActivity.getField) //render with type for game

        setRenderer(mRenderer)

        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            queueEvent(
                object : Runnable {
                    override fun run() {
                        val onScrLoc: IntArray = intArrayOf(0, 0)
                        getLocationOnScreen(onScrLoc)

                        gameActivity.action(
                            mRenderer.traceClick(
                                event.x.toInt() - onScrLoc[0],
                                event.y.toInt() - onScrLoc[1]
                            )
                        )
                    }
                }
            )
        }

        return true
    }
}