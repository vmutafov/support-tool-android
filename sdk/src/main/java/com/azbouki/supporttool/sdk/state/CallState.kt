package com.azbouki.supporttool.sdk.state

import com.azbouki.supporttool.sdk.recording.live.controller.*
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.subjects.PublishSubject

object CallState {
    var twilioRoomName = "vm-test-video-room-3"
    var isInCall = true

    val onCodeSnippetRequest: PublishSubject<CodeSnippetRequest> = PublishSubject.create()
    val onCodeSnippetResponse: PublishSubject<CodeSnippetResponse> = PublishSubject.create()

    val onMetricsRequest: PublishSubject<MetricsRequest> = PublishSubject.create()
    val onMetricsResponse: PublishSubject<MetricsResponse> = PublishSubject.create()

    val onEventData: PublishSubject<EventData> = PublishSubject.create()

    private val gson =
        GsonBuilder().also { it.registerTypeAdapter(Request::class.java, RequestDeserializer()) }
            .create()

    fun registerRawRequest(rawRequest: String) {
        when (val request: Request? = gson.fromJson(rawRequest, Request::class.java)) {
            is MetricsRequest -> onMetricsRequest.onNext(request)
            is CodeSnippetRequest -> onCodeSnippetRequest.onNext(request)
            null -> println("!!! VM: request not parsed: $rawRequest")
        }
    }
}