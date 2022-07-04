package com.azbouki.supporttool.sdk.state

import android.app.Activity
import io.reactivex.rxjava3.subjects.BehaviorSubject

object SupportToolState {

    var isRecording: Boolean = false

    val createdActivitiesFlow: BehaviorSubject<Activity> = BehaviorSubject.create()
    val currentActivity: Activity?
        get() = createdActivitiesFlow.value

    val callState = CallState
    val screenRecordingState = ScreenRecordingState

}