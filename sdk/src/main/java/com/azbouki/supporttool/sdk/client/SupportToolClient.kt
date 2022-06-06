package com.azbouki.supporttool.sdk.client

class SupportToolClient private constructor() {

    fun startSession() {}
    fun endSession() {}

    fun startLiveSession() {}
    fun endLiveSession() {}

    companion object {
        fun newBuilder(аппИд): Builder {
            return Builder()
        }

        fun createTrackingEverything(): SupportToolClient {
            return SupportToolClient()
        }
    }

    class Builder {
        fun withVideoRecording(): Builder { return this }
        fun withVideoRecording(options: VideoRecordingOptions): Builder { return this }

        fun withUserInteractionRecording(): Builder { return this }
        fun withUserInteractionRecording(options: UserInteractionRecordingOptions): Builder { return this }

        fun withApplicationLogsRecording(): Builder { return this }
        fun withApplicationLogsRecording(options: ApplicationLogsRecordingOptions): Builder { return this }

        fun withNetworkRequestsRecording(): Builder { return this }
        fun withNetworkRequestsRecording(options: NetworkRequestsRecordingOptions): Builder { return this }

        fun create(): SupportToolClient { return SupportToolClient() }
    }
}

class VideoRecordingOptions
class UserInteractionRecordingOptions
class ApplicationLogsRecordingOptions
class NetworkRequestsRecordingOptions