package com.azbouki.supporttool.sdk.video.projection

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.window.layout.WindowMetricsCalculator
import com.azbouki.supporttool.sdk.SdkState
import java.io.File

class ScreenRecorder private constructor(private var screenCapturerManager: ScreenCapturerManager?) {

    companion object {
        fun create(context: Context): ScreenRecorder {
            if (Build.VERSION.SDK_INT >= 29) {
                val screenCapturerManager = ScreenCapturerManager(context)
                return ScreenRecorder(screenCapturerManager)
            }
            return ScreenRecorder(null)
        }
    }

    fun start(onStarted: () -> Unit) {
        screenCapturerManager?.startForeground()

        SdkState.withScreenCapturingPermission { mediaProjectionManager, recordingLauncherResult ->
            val outputDir =
                File("${SdkState.currentActivity!!.filesDir.absolutePath}/asd22").apply { mkdirs() }
            val outputFile = File(outputDir, "test.mp4")
            startScreenCapturing(
                mediaProjectionManager,
                recordingLauncherResult,
                outputFile
            )

            onStarted()
        }
    }

    private fun startScreenCapturing(
        mediaProjectionManager: MediaProjectionManager,
        recordingLauncherResult: ActivityResult,
        outputFile: File
    ) {
        val currentActivity = SdkState.currentActivity!!
        val screenBounds = getScreenBounds(currentActivity)
        val density = currentActivity.resources.configuration.densityDpi

        val mediaRecorder = createMediaRecorder(
            currentActivity,
            screenBounds.width(),
            screenBounds.height(),
            outputFile
        )
        val mediaProjection = mediaProjectionManager.getMediaProjection(
            recordingLauncherResult.resultCode,
            recordingLauncherResult.data!!
        )
        val mediaVirtualDisplay = mediaProjection!!.createVirtualDisplay(
            "ScreenCapture",
            screenBounds.width(),
            screenBounds.height(),
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder.surface,
            null,
            null
        )

        mediaRecorder.start()
    }

    private fun createMediaRecorder(
        activity: Activity,
        width: Int,
        height: Int,
        outputFile: File
    ): MediaRecorder {
        val mediaRecorder = if (Build.VERSION.SDK_INT >= 31) {
            MediaRecorder(activity)
        } else {
            @Suppress("DEPRECATION")
            (MediaRecorder())
        }

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setVideoEncodingBitRate(512 * 1000)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoSize(width, height)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(outputFile)
        } else {
            mediaRecorder.setOutputFile(outputFile.absolutePath)
        }

        mediaRecorder.prepare()
        return mediaRecorder
    }

    private fun getScreenBounds(activity: Activity): Rect {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.maximumWindowMetrics.bounds
        } else {
            WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(activity).bounds
        }
    }

    fun stop() {
        screenCapturerManager?.stopForeground()
    }
}