package com.azbouki.supporttool.sdk.recording.live.twilio

import android.widget.Toast
import com.azbouki.supporttool.sdk.state.SupportToolState
import com.twilio.video.*
import java.nio.ByteBuffer

class SupportParticipantListener(onSupportMessage: (String) -> Unit) :
    RemoteParticipant.Listener {

    private val trackListener = RemoteDataTrackListener(onSupportMessage)

    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        showShortToast("onAudioTrackPublished")
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        showShortToast("onAudioTrackUnpublished")
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        showShortToast("onAudioTrackSubscribed")
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        showShortToast("onAudioTrackSubscriptionFailed")
    }

    override fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        showShortToast("onAudioTrackUnsubscribed")
    }

    override fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        showShortToast("onVideoTrackPublished")
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        showShortToast("onVideoTrackUnpublished")
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        showShortToast("onVideoTrackSubscribed")
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        showShortToast("onVideoTrackSubscriptionFailed")
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        showShortToast("onVideoTrackUnsubscribed")
    }

    override fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        showShortToast("onDataTrackPublished")
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        showShortToast("onDataTrackUnpublished")
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        remoteDataTrack.setListener(trackListener)
        showShortToast("onDataTrackSubscribed")
    }

    override fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    ) {
        showShortToast("onDataTrackUnsubscribed")
    }

    override fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        showShortToast("onDataTrackUnsubscribed")
    }

    override fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        showShortToast("onAudioTrackEnabled")
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        showShortToast("onAudioTrackDisabled")
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        showShortToast("onVideoTrackEnabled")
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        showShortToast("onVideoTrackDisabled")
    }

    private fun showShortToast(message: String) {
        Toast.makeText(SupportToolState.currentActivity, message, Toast.LENGTH_SHORT).show()
    }
}

class RemoteDataTrackListener(private val onSupportMessage: (String) -> Unit) :
    RemoteDataTrack.Listener {
    override fun onMessage(remoteDataTrack: RemoteDataTrack, messageBuffer: ByteBuffer) {

    }

    override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
        onSupportMessage(message)
    }
}