package com.moisha.snek.activities.gl.surfaces

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.moisha.snek.activities.gl.LevelPreviewActivity
import com.moisha.snek.graphics.GLRenderer

class PreviewSurface(context: Context) : GLSurfaceView(context) {

    private val previewActivity: LevelPreviewActivity = context as LevelPreviewActivity
    private val mRenderer: GLRenderer

    init {

        setEGLContextClientVersion(2) //OpenGL ES 2.0

        mRenderer = GLRenderer(GLRenderer.TYPE_PREVIEW, previewActivity.getField) //render with type for game

        setRenderer(mRenderer)

        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            queueEvent(
                object : Runnable {
                    override fun run() {
                        previewActivity.finish()
                    }
                }
            )
        }

        return true
    }
}