package com.azbouki.supporttool.sdk.video.screenshot

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.azbouki.supporttool.sdk.utils.EmptyActivityLifecycleCallbacks
import eu.bolt.screenshotty.ScreenshotActionOrder
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder

class AutoScreenshotActivityCallbacks : Application.ActivityLifecycleCallbacks by EmptyActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val screenshotManager = ScreenshotManagerBuilder(activity)
            .withCustomActionOrder(setOf(ScreenshotActionOrder.PixelCopy))
            .build()
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 30)
                screenshotManager
                    .makeScreenshot()
                    .observe(
                        onSuccess = { screenshot ->
                            if (screenshot is ScreenshotBitmap) {

                            }
                        }, onError = { throwable ->

                        }
                    )
            }
        }
        handler.post(runnable)
    }
}