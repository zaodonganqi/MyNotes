package com.listener

import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class ItemViewTouchListener(val wl: WindowManager.LayoutParams, val windowManager: WindowManager): View.OnTouchListener {
    private var x = 0
    private var y = 0
    private var vWidth = 0
    private var vHeight = 0
    private var isPulled = false
    private var location = IntArray(2)
    private var screenWidth = 0
    private var screenHeight = 0
    private var maxX = 0
    private var maxY = 0
    private var minX = 0
    private var minY = 0

    init {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                x = motionEvent.rawX.toInt()
                y = motionEvent.rawY.toInt()
                vWidth = view.width
                vHeight = view.height
                maxX = screenWidth / 2 - vWidth / 2
                maxY = screenHeight / 2 - vHeight / 2
                minX = vWidth / 2 - screenWidth / 2
                minY = vHeight / 2 - screenHeight / 2
                view.getLocationOnScreen(location)
                val viewX = location[0]
                val viewY = location[1]
                //判断是否为放大
                isPulled = (x - viewX) in (vWidth - 100)..vWidth && (y - viewY) in (vHeight - 100)..vHeight
            }
            MotionEvent.ACTION_MOVE -> {
                val nowX = motionEvent.rawX.toInt()
                val nowY = motionEvent.rawY.toInt()
                val movedX = nowX - x
                val movedY = nowY - y
                x = nowX
                y = nowY

                if (!isPulled) {
                    //拖动悬浮窗
                    if (wl.x + movedX in minX..maxX && wl.y + movedY in minY..maxY) {
                        wl.apply {
                            x += movedX
                            y += movedY
                        }
                    } else if (wl.x + movedX >= maxX && wl.y + movedY in minY..maxY) {
                        wl.apply {
                            x = maxX
                            y += movedY
                        }
                    } else if (wl.x + movedX <= maxX && wl.y + movedY in minY..maxY) {
                        wl.apply {
                            x = minX
                            y += movedY
                        }
                    } else if (wl.y + movedY >= maxY && wl.x + movedX in minX..maxX) {
                        wl.apply {
                            x += movedX
                            y = maxY
                        }
                    } else if (wl.y + movedY <= maxY && wl.x + movedX in minX..maxX) {
                        wl.apply {
                            x += movedX
                            y = minY
                        }
                    }

                    //更新悬浮球控件位置
                    windowManager?.updateViewLayout(view, wl)
                } else {
                    if (wl.width <= 200 || wl.height <= 200) {
                        wl.apply {
                            width = vWidth
                            height = vHeight
                        }
                    }
                    var params = view.layoutParams
                    var move = Math.min(movedX,movedY)
                    params.width += move
                    params.height += move
                    view.layoutParams = params
                }
                windowManager?.updateViewLayout(view, wl)
            }
            else -> {

            }
        }
        return false
    }
}