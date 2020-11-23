package com.moisha.snek.activities.gl.surfaces

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.moisha.snek.activities.gl.EditorActivity
import com.moisha.snek.graphics.GLRenderer

class EditorSurface constructor(context: Context) : GLSurfaceView(context) {

    private val mRenderer: GLRenderer
    private val editorActivity: EditorActivity = context as EditorActivity

    init {

        setEGLContextClientVersion(2) //OpenGL ES 2.0

        mRenderer = GLRenderer(GLRenderer.TYPE_EDITOR_RENDER, editorActivity.getField)

        setRenderer(mRenderer)

        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY //render only when requested

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            queueEvent(
                object : Runnable {
                    override fun run() {
                        val onScrLoc: IntArray = intArrayOf(0, 0)
                        getLocationOnScreen(onScrLoc)

                        editorActivity.action(
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