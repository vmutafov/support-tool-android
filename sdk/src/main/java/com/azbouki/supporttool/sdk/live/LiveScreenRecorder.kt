package com.azbouki.supporttool.sdk.live

import android.content.Context
import android.os.Build
import com.azbouki.supporttool.sdk.SdkState
import com.azbouki.supporttool.sdk.video.projection.ScreenCapturerManager

class LiveScreenRecorder private constructor(private var screenCapturerManager: ScreenCapturerManager?) {

    private var debugSessionCreator: DebugSessionCreator? = null

    companion object {
        fun create(context: Context): LiveScreenRecorder {
            if (Build.VERSION.SDK_INT >= 29) {
                val screenCapturerManager = ScreenCapturerManager(context)
                return LiveScreenRecorder(screenCapturerManager)
            }
            return LiveScreenRecorder(null)
        }
    }

    fun start(onStarted: () -> Unit) {
        screenCapturerManager?.startForeground()

        SdkState.withScreenCapturingPermission { mediaProjectionManager, recordingLauncherResult ->
            debugSessionCreator = DebugSessionCreator().also {
                val currentActivity = SdkState.currentActivity!!
                it.connectToSupport(
                    currentActivity,
                    recordingLauncherResult,
                    SdkState.twilioRoomName
                )
            }

            onStarted()
        }
    }

    fun stop() {
        debugSessionCreator?.disconnectFromSupport()
        screenCapturerManager?.stopForeground()
    }
}