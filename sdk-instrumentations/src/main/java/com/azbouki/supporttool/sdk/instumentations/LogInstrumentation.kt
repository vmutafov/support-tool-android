package com.azbouki.supporttool.sdk.instumentations

import android.util.Log
import io.sentry.Breadcrumb
import io.sentry.IHub
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message

object LogInstrumentation {

    private var hub: IHub? = null
    private var minEventLevel: SentryLevel = SentryLevel.ERROR
    private var minBreadcrumbLevel: SentryLevel = SentryLevel.INFO

    fun configure(hub: IHub, minEventLevel: SentryLevel, minBreadcrumbLevel: SentryLevel) {
        this.hub = hub
        this.minEventLevel = minEventLevel
        this.minBreadcrumbLevel = minBreadcrumbLevel
    }

    const val VERBOSE: Int = 2
    const val DEBUG: Int = 3
    const val INFO: Int = 4
    const val WARN: Int = 5
    const val ERROR: Int = 6
    const val ASSERT: Int = 7

    @JvmStatic
    fun v(tag: String?, msg: String): Int {
        logWithSentry(VERBOSE, null, tag, msg)
        return Log.v(tag, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?, tr: Throwable?): Int {
        logWithSentry(VERBOSE, tr, tag, msg)
        return Log.v(tag, msg, tr)
    }

    @JvmStatic
    fun d(tag: String?, msg: String): Int {
        return Log.d(tag, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: String?, tr: Throwable?): Int {
        return Log.d(tag, msg, tr)
    }

    @JvmStatic
    fun i(tag: String?, msg: String): Int {
        logWithSentry(INFO, null, tag, msg)
        return Log.i(tag, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: String?, tr: Throwable?): Int {
        return Log.i(tag, msg, tr)
    }

    @JvmStatic
    fun w(tag: String?, msg: String): Int {
        return Log.w(tag, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: String?, tr: Throwable?): Int {
        return Log.w(tag, msg, tr)
    }

    @JvmStatic
    fun isLoggable(tag: String?, level: Int): Boolean {
        return Log.isLoggable(tag, level)
    }

    @JvmStatic
    fun w(tag: String?, tr: Throwable?): Int {
        return Log.w(tag, tr)
    }

    @JvmStatic
    fun e(tag: String?, msg: String): Int {
        return Log.e(tag, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: String?, tr: Throwable?): Int {
        return Log.e(tag, msg, tr)
    }

    @JvmStatic
    fun wtf(tag: String?, msg: String?): Int {
        return Log.wtf(tag, msg)
    }

    @JvmStatic
    fun wtf(tag: String?, tr: Throwable): Int {
        return Log.wtf(tag, tr)
    }

    @JvmStatic
    fun wtf(tag: String?, msg: String?, tr: Throwable?): Int {
        return Log.wtf(tag, msg, tr)
    }

    @JvmStatic
    fun getStackTraceString(tr: Throwable?): String {
        return Log.getStackTraceString(tr)
    }

    @JvmStatic
    fun println(priority: Int, tag: String?, msg: String): Int {
        return Log.println(priority, tag, msg)
    }

    private fun logWithSentry(
        priority: Int,
        throwable: Throwable?,
        tag: String?,
        message: String?,
    ) {
        if (message.isNullOrEmpty() && throwable == null) {
            return // Swallow message if it's null and there's no throwable
        }

        val level = getSentryLevel(priority)
        val sentryMessage = Message().apply {
            this.message = "$tag: $message"
        }

        captureEvent(level, sentryMessage, throwable)
        addBreadcrumb(level, sentryMessage, throwable)
    }

    /**
     * do not log if it's lower than min. required level.
     */
    private fun isLoggable(
        level: SentryLevel,
        minLevel: SentryLevel
    ): Boolean = level.ordinal >= minLevel.ordinal

    /**
     * Captures an event with the given attributes
     */
    private fun captureEvent(
        sentryLevel: SentryLevel,
        msg: Message,
        throwable: Throwable?
    ) {
        val thisHub = hub
        if (thisHub != null && isLoggable(sentryLevel, minEventLevel)) {
            val sentryEvent = SentryEvent().apply {
                level = sentryLevel
                throwable?.let { setThrowable(it) }
                message = msg
                logger = "Timber"
            }

            thisHub.captureEvent(sentryEvent)
        }
    }

    /**
     * Adds a breadcrumb
     */
    private fun addBreadcrumb(
        sentryLevel: SentryLevel,
        msg: Message,
        throwable: Throwable?
    ) {
        val thisHub = hub
        // checks the breadcrumb level and hub
        if (thisHub != null && isLoggable(sentryLevel, minBreadcrumbLevel)) {
            val throwableMsg = throwable?.message
            val breadCrumb = when {
                msg.message != null -> Breadcrumb().apply {
                    level = sentryLevel
                    category = "Log"
                    message = msg.formatted ?: msg.message
                }
                throwableMsg != null -> Breadcrumb.error(throwableMsg).apply {
                    category = "exception"
                }
                else -> null
            }

            breadCrumb?.let { thisHub.addBreadcrumb(it) }
        }
    }

    /**
     * Converts from Timber priority to SentryLevel.
     * Fallback to SentryLevel.DEBUG.
     */
    private fun getSentryLevel(priority: Int): SentryLevel {
        return when (priority) {
            Log.ASSERT -> SentryLevel.FATAL
            Log.ERROR -> SentryLevel.ERROR
            Log.WARN -> SentryLevel.WARNING
            Log.INFO -> SentryLevel.INFO
            Log.DEBUG -> SentryLevel.DEBUG
            Log.VERBOSE -> SentryLevel.DEBUG
            else -> SentryLevel.DEBUG
        }
    }
}