package com.azbouki.supporttool.sdk

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.azbouki.supporttool.sdk.live.controller.*
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.io.File
import java.util.WeakHashMap

object SdkState {
    var isInTwilioMode = true
    var twilioRoomName = "vm-test-video-room-3"

    val currentActivity: Activity?
        get() = createdActivitiesFlow.value

    var isRecording: Boolean = false

    val createdActivitiesFlow: BehaviorSubject<Activity> = BehaviorSubject.create()

    val callState = CallState

    // TODO: use a global MediaProjectionManager and related classes for all activities
    private val activitiesMediaLaunchers: WeakHashMap<Activity, ActivityResultLauncher<Intent>> =
        WeakHashMap()
    private val activitiesMediaProjectionManagers: WeakHashMap<Activity, MediaProjectionManager?> =
        WeakHashMap()
    private val activitiesMediaLauncherResults: WeakHashMap<Activity, BehaviorSubject<ActivityResult>> =
        WeakHashMap()

    fun registerActivityMediaLauncher(
        activity: Activity,
        mediaLauncher: ActivityResultLauncher<Intent>
    ) {
        activitiesMediaLaunchers[activity] = mediaLauncher
    }

    fun registerEventualActivityMediaLauncherResult(activity: Activity) {
        activitiesMediaLauncherResults[activity] = BehaviorSubject.create()
    }

    fun registerActivityMediaLauncherResult(activity: Activity, result: ActivityResult) {
        activitiesMediaLauncherResults[activity]!!.onNext(result)
    }

    fun getActivityMediaLauncherResultForCurrentActivity(): BehaviorSubject<ActivityResult> {
        return activitiesMediaLauncherResults[currentActivity!!]!!
    }

    fun getMediaLauncherForCurrentActivity(): ActivityResultLauncher<Intent>? =
        activitiesMediaLaunchers[currentActivity!!]

    fun getOrCreateMediaProjectionManagetForCurrentActivity(): MediaProjectionManager? {
        return activitiesMediaProjectionManagers[currentActivity!!]
            ?: ContextCompat.getSystemService(
                currentActivity!!,
                MediaProjectionManager::class.java
            ).also { activitiesMediaProjectionManagers[currentActivity!!] = it }

    }

    fun withScreenCapturingPermission(block: (MediaProjectionManager, ActivityResult) -> Unit) {
        val mediaLauncher = getMediaLauncherForCurrentActivity()
        if (mediaLauncher != null) {
            val currentMediaProjectionManager = getOrCreateMediaProjectionManagetForCurrentActivity()
            if (currentMediaProjectionManager != null) {
                mediaLauncher.launch(currentMediaProjectionManager.createScreenCaptureIntent())
                getActivityMediaLauncherResultForCurrentActivity()
                    .subscribe { recordingLauncherResult ->
                        block(currentMediaProjectionManager, recordingLauncherResult)
                    }
            } else {
                throw RuntimeException("Could not get media projection manager")
            }
        } else {
            throw RuntimeException("Could not get media launcher")
        }
    }

}

object CallState {
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