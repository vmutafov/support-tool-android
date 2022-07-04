package com.azbouki.supporttool.sdk.recording.sentry.integration

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.EditText
import androidx.core.view.allViews
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.azbouki.supporttool.sdk.state.SupportToolState
import com.azbouki.supporttool.sdk.utils.getResourceId
import io.reactivex.rxjava3.disposables.Disposable
import io.sentry.Breadcrumb
import io.sentry.IHub
import io.sentry.Integration
import io.sentry.SentryOptions
import java.io.Closeable
import java.util.*

class TextIntegration() : Integration, Closeable {
    private val iteratedViewsCache = Collections.newSetFromMap(WeakHashMap<View, Boolean>())
    private var subscription: Disposable? = null

    override fun register(hub: IHub, options: SentryOptions) {
        subscription = SupportToolState.createdActivitiesFlow.subscribe { activity ->
            if (activity is FragmentActivity) {
                instrumentFragmentViews(activity, hub)
            } else {
                instrumentLegacyFragmentViews(activity, hub)
            }
            activity
                .window
                .decorView
                .performAllViewsInstrumentation(hub)
        }
    }

    private fun instrumentFragmentViews(activity: FragmentActivity, hub: IHub) {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)
                f.view?.performAllViewsInstrumentation(hub)
            }
        }, true)
    }

    private fun instrumentLegacyFragmentViews(activity: Activity, hub: IHub) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.fragmentManager.registerFragmentLifecycleCallbacks(object :
                android.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(
                    fm: android.app.FragmentManager?,
                    f: android.app.Fragment?
                ) {
                    super.onFragmentStarted(fm, f)
                    f?.view?.performAllViewsInstrumentation(hub)
                }
            }, true)
        }
    }

    private fun View.performAllViewsInstrumentation(hub: IHub) {
        this.allViews
            .forEach { view -> performViewInstrumentation(hub, view) }
    }

    private fun performViewInstrumentation(hub: IHub, view: View) {
        if (!iteratedViewsCache.contains(view)) {
            if (view is EditText) {
                view.addTextChangedListener { editable ->
                    val text = editable.toString()
                    addBreadcrumb(
                        hub = hub,
                        target = view,
                        eventType = "text-input",
                        additionalData = mapOf("text" to text)
                    )
                }
            }
            iteratedViewsCache.add(view)
        }
    }

    private fun addBreadcrumb(
        hub: IHub,
        target: View,
        eventType: String,
        additionalData: Map<String, Any>
    ) {
        val className: String
        val canonicalName = target.javaClass.canonicalName
        className = canonicalName ?: target.javaClass.simpleName
        hub.addBreadcrumb(
            Breadcrumb.userInteraction(
                eventType, getResourceId(target), className, additionalData
            )
        )
    }

    override fun close() {
        subscription?.dispose()
    }
}