package com.azbouki.supporttool.sdk.recording.sentry.integration

import com.azbouki.supporttool.sdk.instumentations.LogInstrumentation
import io.sentry.IHub
import io.sentry.Integration
import io.sentry.SentryLevel
import io.sentry.SentryOptions

class LogIntegration(
    private val minEventLevel: SentryLevel = SentryLevel.ERROR,
    private val minBreadcrumbLevel: SentryLevel = SentryLevel.INFO
) : Integration {

    override fun register(hub: IHub, options: SentryOptions) {
        LogInstrumentation.configure(hub, minEventLevel, minBreadcrumbLevel)
    }
}