package com.azbouki.supporttool.sdk.sentry.integration

import android.app.Activity
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.azbouki.supporttool.sdk.SdkState
import io.sentry.IHub
import io.sentry.Integration
import io.sentry.SentryOptions

class VideoIntegration : Integration {
    override fun register(hub: IHub, options: SentryOptions) {
        SdkState.createdActivitiesFlow
            .subscribe { activity ->
                if (activity is FragmentActivity) {
                    SdkState.registerEventualActivityMediaLauncherResult(activity)
                    val mediaProjectionLauncher = activity.registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result: ActivityResult ->
                        SdkState.registerActivityMediaLauncherResult(activity, result)
                    }
                    SdkState.registerActivityMediaLauncher(activity, mediaProjectionLauncher)
                }
            }
    }
}