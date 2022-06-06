package com.azbouki.supporttool.sdk.sentry

import android.app.Application
import com.azbouki.supporttool.sdk.SdkState
import com.azbouki.supporttool.sdk.live.controller.EventData
import com.azbouki.supporttool.sdk.sentry.integration.LogIntegration
import com.azbouki.supporttool.sdk.sentry.integration.TextIntegration
import com.azbouki.supporttool.sdk.sentry.integration.VideoIntegration
import com.google.gson.GsonBuilder
import io.sentry.IHub
import io.sentry.Integration
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions

object SentryInitFacade {
    fun init(applicationContext: Application) {
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
                    if (SdkState.isRecording) event else null
                }

            options.setBeforeBreadcrumb { breadcrumb, hint ->
                if (SdkState.isInTwilioMode) {
                    val eventData = EventData(breadcrumb)
                    SdkState.callState.onEventData.onNext(eventData)
                }
                if (SdkState.isRecording) breadcrumb else null
            }

//            options.setTransportFactory { options, requestDetails ->
//                FirebaseTransport(options.serializer)
//            }

            options.isEnableAutoSessionTracking = false
            options.maxBreadcrumbs = 1000 // TODO: check if 1000 is recognized by Sentry and there is no hardcoded limit somewhere in their SDK
        }
    }
}