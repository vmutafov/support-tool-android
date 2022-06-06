package com.azbouki.supporttool.sdk

import android.app.Application
import com.azbouki.supporttool.sdk.live.LiveScreenRecorder
import com.azbouki.supporttool.sdk.sentry.SentryInitFacade
import com.azbouki.supporttool.sdk.utils.OnActivityCreatedLifecycleCallback
import com.azbouki.supporttool.sdk.video.projection.ScreenRecorder
import io.sentry.Sentry


object SupportTool {

    private var liveScreenRecorder: LiveScreenRecorder? = null
    private var screenRecorder: ScreenRecorder? = null

    fun init(applicationContext: Application, supportToolKey: String) {
        applicationContext.registerActivityLifecycleCallbacks(
            OnActivityCreatedLifecycleCallback { activity, bundle ->
                SdkState.createdActivitiesFlow.onNext(activity)
            }
        )

        SentryInitFacade.init(applicationContext)
        screenRecorder = ScreenRecorder.create(applicationContext)
        liveScreenRecorder = LiveScreenRecorder.create(applicationContext)
    }

    fun start() {
        val startSessionCallback = {
            SdkState.isRecording = true
            Sentry.startSession()
            Sentry.clearBreadcrumbs()
            Sentry.addBreadcrumb("=== Start Session ===")
        }

        if (SdkState.isInTwilioMode) {
            liveScreenRecorder!!.start(startSessionCallback)
        } else {
            screenRecorder!!.start(startSessionCallback)
        }
    }



    fun stop() {
        Sentry.addBreadcrumb("=== End Session ===")
        Sentry.captureMessage("SESSION_RECORDED")
        Sentry.endSession()
        liveScreenRecorder?.stop()
        screenRecorder?.stop()
        SdkState.isRecording = false
    }

}