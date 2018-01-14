package com.lucasmatt.antilibrary.wallpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.lucasmatt.antilibrary.goodreads.Goodreads

class AntilibraryWallpaperService : WallpaperService() {

    val goodreads = Goodreads("61058109-matthew-lucas")

    private var mVisible: Boolean = false  // visible flag
    var canvas: Canvas? = null      // canvas reference
    var Drawspeed = 10L   // thread call delay time
    var mcontext: Context? = null   //reference to the current context

    override fun onCreateEngine(): Engine {
        return AntilibraryWallpaperEngine()
    }

    private inner class AntilibraryWallpaperEngine : WallpaperService.Engine() {

        val mHandler = Handler() // this is to handle the thread

        //the tread responsibe for drawing this thread get calls every time
        // drawspeed vars set the execution speed
        private val mDrawFrame = Runnable {
            // This method get called each time to drwaw thw frame
            // Engine class does not provide any invlidate methods
            // as used in canvas
            // set your draw call here
            drawFrame()
        }


        //Called when the surface is created
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            //call the draw method
            // this is where you must call your draw code
            drawFrame()

        }

        // remove thread
        override fun onDestroy() {
            super.onDestroy()
            mHandler.removeCallbacks(mDrawFrame)
        }


        //called when varaible changed
        override fun onVisibilityChanged(visible: Boolean) {
            mVisible = visible
            if (visible) {

                //call the drawFunction
                drawFrame()

            } else {

                //this is necessay to remove the call back
                mHandler.removeCallbacks(mDrawFrame)
            }
        }

        //called when surface destroyed
        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mVisible = false
            //this is necessay to remove the call back
            mHandler.removeCallbacks(mDrawFrame)
        }


        // my function which contain the code to draw
        //this function contain the the main draw call
        /// this function need to call every time the code is executed
        // the thread call this functioin with some delay "drawspeed"
        fun drawFrame() {
            //getting the surface holder
            val holder = surfaceHolder

            canvas = null  // canvas
            try {
                canvas = holder.lockCanvas()  //get the canvas
                if (canvas != null) {
//                    canvas?.drawColor(Color.GREEN)

                    val shelf = goodreads.shelves().get(0)
                    val img = goodreads.contentsOf(shelf).get(0).asBitmap(applicationContext)

                    val paint = Paint()
                    paint.color = Color.WHITE
                    paint.setAntiAlias(true);
                    canvas?.drawBitmap(img, 0f, 0f, paint)
//                    canvas?.drawCircle(0f, 0f, 100.0f, paint)
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }

            // Reschedule the next redraw
            // this is the replacement for the invilidate funtion
            // every time call the drawFrame to draw the matrix
            mHandler.removeCallbacks(mDrawFrame)
            if (mVisible) {
                // set the execution delay
                mHandler.postDelayed(mDrawFrame, Drawspeed)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            // update when surface changed

        }

    }

}


