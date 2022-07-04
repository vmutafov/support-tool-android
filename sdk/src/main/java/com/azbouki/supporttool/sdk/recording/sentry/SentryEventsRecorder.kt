package com.azbouki.supporttool.sdk.recording.sentry

import android.app.Application
import com.azbouki.supporttool.sdk.recording.live.controller.EventData
import com.azbouki.supporttool.sdk.recording.sentry.integration.LogIntegration
import com.azbouki.supporttool.sdk.recording.sentry.integration.TextIntegration
import com.azbouki.supporttool.sdk.recording.sentry.integration.VideoIntegration
import com.azbouki.supporttool.sdk.state.SupportToolState
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions
import okhttp3.OkHttpClient

class SentryEventsRecorder private constructor() {
    companion object {
        fun create(applicationContext: Application): SentryEventsRecorder {
            SentryAndroid.init(applicationContext) { options: SentryAndroidOptions ->
//            options.dsn = "https://examplePublicKey@o0.ingest.sentry.io/0"
                options.dsn =
                    "https://c7d891240c3b4a70a4aae90d3c71d9b3@o1173130.ingest.sentry.io/6268024"
                options.enableAllAutoBreadcrumbs(true)
                options.addIntegration(LogIntegration())
                options.addIntegration(TextIntegration())
                options.addIntegration(VideoIntegration())
                options.beforeSend =
                    SentryOptions.BeforeSendCallback { event: SentryEvent, hint: Any? ->
                        if (SupportToolState.isRecording) event else null
                    }

                options.setBeforeBreadcrumb { breadcrumb, hint ->
                    if (SupportToolState.callState.isInCall) {
                        val eventData = EventData(breadcrumb)
                        SupportToolState.callState.onEventData.onNext(eventData)
                    }
                    if (SupportToolState.isRecording) {
                        println("!!!! VM: " + breadcrumb.message)
                    }
//                    if (SupportToolState.isRecording) breadcrumb else null
                    breadcrumb
                }

//                options.setTransportFactory { options, requestDetails ->
//                    FirebaseTransport(options.serializer)
//                }

                options.isEnableAutoSessionTracking = false
                options.maxBreadcrumbs =
                    1000 // TODO: check if 1000 is recognized by Sentry and there is no hardcoded limit somewhere in their SDK
            }
            return SentryEventsRecorder()
        }
    }

    fun start() {
        Sentry.clearBreadcrumbs()
//        Sentry.startSession()
        Sentry.addBreadcrumb("=== Start Session ===")
    }

    fun stop() {
        Sentry.addBreadcrumb("=== End Session ===")
        Sentry.captureMessage("SESSION_RECORDED")
//        Sentry.endSession()
    }
}