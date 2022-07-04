package com.azbouki.supporttool.sdk.recording.live

import android.content.Context
import android.os.Build
import com.azbouki.supporttool.sdk.state.SupportToolState
import com.azbouki.supporttool.sdk.recording.video.ScreenCapturerManager

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

        SupportToolState.screenRecordingState.withScreenCapturingPermission { mediaProjectionManager, recordingLauncherResult ->
            debugSessionCreator = DebugSessionCreator().also {
                val currentActivity = SupportToolState.currentActivity!!
                it.connectToSupport(
                    currentActivity,
                    recordingLauncherResult,
                    SupportToolState.callState.twilioRoomName
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