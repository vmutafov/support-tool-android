package com.azbouki.supporttool.sdk.live.twilio

import android.widget.Toast
import com.azbouki.supporttool.sdk.SdkState
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException

class SupportCallRoomListener(private val onSupportMessage: (String) -> Unit) : Room.Listener {

    private val participantListener = SupportParticipantListener(onSupportMessage)

    override fun onConnected(room: Room) {
        showShortToast("onConnected")
        room.remoteParticipants.forEach {
            it.setListener(participantListener)
        }
    }

    override fun onConnectFailure(room: Room, twilioException: TwilioException) {
        showShortToast("onConnectFailure")
    }

    override fun onReconnecting(room: Room, twilioException: TwilioException) {
        showShortToast("onReconnecting")
    }

    override fun onReconnected(room: Room) {
        showShortToast("onReconnected")
    }

    override fun onDisconnected(room: Room, twilioException: TwilioException?) {
        showShortToast("onDisconnected")
    }

    override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
        showShortToast("onParticipantConnected")
        remoteParticipant.setListener(participantListener)
    }

    override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
        showShortToast("onParticipantDisconnected")
    }

    override fun onRecordingStarted(room: Room) {
    }

    override fun onRecordingStopped(room: Room) {
    }

    private fun showShortToast(message: String) {
        Toast.makeText(SdkState.currentActivity, message, Toast.LENGTH_SHORT).show()
    }
}