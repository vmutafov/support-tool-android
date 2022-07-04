package com.azbouki.supporttool.sdk.recording.live

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.azbouki.supporttool.sdk.state.SupportToolState
import com.azbouki.supporttool.sdk.recording.live.controller.CodeSnippetResponse
import com.azbouki.supporttool.sdk.recording.live.controller.JSExecutor
import com.azbouki.supporttool.sdk.recording.live.controller.MetricsResponse
import com.azbouki.supporttool.sdk.recording.live.twilio.SupportCallRoomListener
import com.azbouki.supporttool.sdk.recording.live.twilio.TwilioTokenService
import com.google.gson.GsonBuilder
import com.twilio.video.*
import io.reactivex.rxjava3.disposables.Disposable

class DebugSessionCreator {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val twilioTokenService = TwilioTokenService()

    private var room: Room? = null
    private var codeSnippetRequestsSubscription: Disposable? = null
    private var metricsRequestsSubscription: Disposable? = null
    private var eventDataSubscription: Disposable? = null

    fun connectToSupport(
        activity: Activity,
        recordingLauncherResult: ActivityResult,
        roomName: String?
    ) {
        disconnectFromSupport()

        val screenCapturer = ScreenCapturer(
            activity,
            recordingLauncherResult.resultCode,
            recordingLauncherResult.data!!,
            null
        )

        val screenVideoTrack = LocalVideoTrack.create(activity, true, screenCapturer)
        val dataTrack = LocalDataTrack.create(activity)

        twilioTokenService.getTwilioVideoToken(onSuccess = { token ->
            val connectOptions = ConnectOptions.Builder(token)
                .roomName(roomName!!)
                .videoTracks(listOf(screenVideoTrack))
                .dataTracks(listOf(dataTrack))
                .build()

            val supportCallRoomListener =
                SupportCallRoomListener(SupportToolState.callState::registerRawRequest)

            Video.connect(activity, connectOptions, supportCallRoomListener)
            val jsExecutor = JSExecutor(activity)

            codeSnippetRequestsSubscription =
                SupportToolState.callState.onCodeSnippetRequest.subscribe { codeSnippetRequest ->
                    val executionResult = jsExecutor.executeJSCode(codeSnippetRequest.code)
                    val codeSnippetResponse = CodeSnippetResponse(
                        codeSnippetRequest.requestId,
                        executionResult.hasError,
                        executionResult.result
                    )
                    SupportToolState.callState.onCodeSnippetResponse.onNext(codeSnippetResponse)
                }

            metricsRequestsSubscription =
                SupportToolState.callState.onMetricsRequest.subscribe { metricsRequest ->
                    val metricsResponse = MetricsResponse(metricsRequest.requestId, "test")
                    SupportToolState.callState.onMetricsResponse.onNext(metricsResponse)
                }

            eventDataSubscription = SupportToolState.callState.onEventData.subscribe { eventData ->
                val serializedEventData = gson.toJson(eventData)
                dataTrack?.send(serializedEventData)
            }
        },
        onFailure = {
            Toast.makeText(activity, "Video token request failed", Toast.LENGTH_LONG).show()
        })


    }

    fun disconnectFromSupport() {
        room?.disconnect()
        codeSnippetRequestsSubscription?.dispose()
        metricsRequestsSubscription?.dispose()
        eventDataSubscription?.dispose()
    }
}