package com.azbouki.supporttool.sdk.recording.sentry.integration

import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.azbouki.supporttool.sdk.state.SupportToolState
import io.sentry.IHub
import io.sentry.Integration
import io.sentry.SentryOptions

class VideoIntegration : Integration {
    override fun register(hub: IHub, options: SentryOptions) {
        SupportToolState.createdActivitiesFlow
            .subscribe { activity ->
                if (activity is FragmentActivity) {
                    SupportToolState.screenRecordingState.registerEventualActivityMediaLauncherResult(activity)
                    val mediaProjectionLauncher = activity.registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result: ActivityResult ->
                        SupportToolState.screenRecordingState.registerActivityMediaLauncherResult(activity, result)
                    }
                    SupportToolState.screenRecordingState.registerActivityMediaLauncher(activity, mediaProjectionLauncher)
                }
            }
    }
}