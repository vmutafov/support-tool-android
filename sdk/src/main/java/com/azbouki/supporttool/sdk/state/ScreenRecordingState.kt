package com.azbouki.supporttool.sdk.state

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*

object ScreenRecordingState {
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
        return activitiesMediaLauncherResults[SupportToolState.currentActivity!!]!!
    }

    fun getMediaLauncherForCurrentActivity(): ActivityResultLauncher<Intent>? =
        activitiesMediaLaunchers[SupportToolState.currentActivity!!]

    fun getOrCreateMediaProjectionManagetForCurrentActivity(): MediaProjectionManager? {
        return activitiesMediaProjectionManagers[SupportToolState.currentActivity!!]
            ?: ContextCompat.getSystemService(
                SupportToolState.currentActivity!!,
                MediaProjectionManager::class.java
            ).also { activitiesMediaProjectionManagers[SupportToolState.currentActivity!!] = it }

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