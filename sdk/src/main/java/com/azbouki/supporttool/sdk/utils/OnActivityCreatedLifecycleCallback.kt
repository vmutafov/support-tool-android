package com.azbouki.supporttool.sdk.utils

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

class OnActivityCreatedLifecycleCallback(private val onCreated: (activity: Activity, bundle: Bundle?) -> Unit) :
    ActivityLifecycleCallbacks by EmptyActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        onCreated(activity, bundle)
    }
}