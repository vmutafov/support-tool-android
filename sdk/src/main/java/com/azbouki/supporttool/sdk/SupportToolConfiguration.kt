package com.azbouki.supporttool.sdk

class SupportToolConfiguration {
    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }

        fun createTrackingEverything(): SupportToolConfiguration {
            return Builder()
                .withVideoRecording()
                .withUserInteractionRecording()
                .withApplicationLogsRecording()
                .withNetworkRequestsRecording()
                .create()
        }
    }

    class Builder {

        fun withVideoRecording(): Builder {
            return this
        }

        fun withVideoRecording(options: VideoRecordingOptions): Builder {
            return this
        }

        fun withUserInteractionRecording(): Builder {
            return this
        }

        fun withUserInteractionRecording(options: UserInteractionRecordingOptions): Builder {
            return this
        }

        fun withApplicationLogsRecording(): Builder {
            return this
        }

        fun withApplicationLogsRecording(options: ApplicationLogsRecordingOptions): Builder {
            return this
        }

        fun withNetworkRequestsRecording(): Builder {
            return this
        }

        fun withNetworkRequestsRecording(options: NetworkRequestsRecordingOptions): Builder {
            return this
        }

        fun create(): SupportToolConfiguration {
            return SupportToolConfiguration()
        }
    }
}