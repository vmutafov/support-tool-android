package com.azbouki.supporttool.sdk

import android.app.Application
import com.azbouki.supporttool.sdk.recording.live.LiveScreenRecorder
import com.azbouki.supporttool.sdk.recording.sentry.SentryEventsRecorder
import com.azbouki.supporttool.sdk.recording.video.ScreenRecorder
import com.azbouki.supporttool.sdk.state.SupportToolState
import com.azbouki.supporttool.sdk.utils.OnActivityCreatedLifecycleCallback

object SupportTool {

    private lateinit var sentryRecorder: SentryEventsRecorder
    private lateinit var screenRecorder: ScreenRecorder
    private lateinit var liveScreenRecorder: LiveScreenRecorder

    fun init(
        supportToolKey: String,
        appId: String,
        application: Application,
        configuration: SupportToolConfiguration = SupportToolConfiguration.createTrackingEverything()
    ) {
        application.registerActivityLifecycleCallbacks(
            OnActivityCreatedLifecycleCallback { activity, bundle ->
                SupportToolState.createdActivitiesFlow.onNext(activity)
            }
        )
        liveScreenRecorder = LiveScreenRecorder.create(application)
        screenRecorder = ScreenRecorder.create(application)
        sentryRecorder = SentryEventsRecorder.create(application)
    }

    fun startSession() {
        SupportToolState.isRecording = true
        screenRecorder.start(sentryRecorder::start)
    }

    fun stopSession() {
        screenRecorder.stop()
        sentryRecorder.stop()
        SupportToolState.isRecording = false
    }

    fun startLiveSession() {
        SupportToolState.isRecording = true
        liveScreenRecorder.start(sentryRecorder::start)
    }

    fun stopLiveSession() {
        liveScreenRecorder.stop()
        sentryRecorder.stop()
        SupportToolState.isRecording = false
    }

}

class VideoRecordingOptions
class UserInteractionRecordingOptions
class ApplicationLogsRecordingOptions
class NetworkRequestsRecordingOptions