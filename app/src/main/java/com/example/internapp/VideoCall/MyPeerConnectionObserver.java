package com.example.internapp.VideoCall;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

/**
 * MyPeerConnectionObserver class implements the PeerConnection.Observer interface
 * to observe events related to WebRTC peer connections.
 */
public class MyPeerConnectionObserver implements PeerConnection.Observer {

    /**
     * This method is called when the signaling state changes.
     *
     * @param signalingState The new signaling state.
     */
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when the ICE connection state changes.
     *
     * @param iceConnectionState The new ICE connection state.
     */
    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when the ICE connection receiving state changes.
     *
     * @param receiving If ICE connection receiving state changes to receiving.
     */
    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when the ICE gathering state changes.
     *
     * @param iceGatheringState The new ICE gathering state.
     */
    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when an ICE candidate is generated.
     *
     * @param iceCandidate The new ICE candidate.
     */
    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when ICE candidates are removed.
     *
     * @param iceCandidates The ICE candidates to be removed.
     */
    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when a media stream is added.
     *
     * @param mediaStream The new media stream.
     */
    @Override
    public void onAddStream(MediaStream mediaStream) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when a media stream is removed.
     *
     * @param mediaStream The removed media stream.
     */
    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when a data channel is created.
     *
     * @param dataChannel The newly created data channel.
     */
    @Override
    public void onDataChannel(DataChannel dataChannel) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when renegotiation is needed.
     */
    @Override
    public void onRenegotiationNeeded() {
        // No action needed in this implementation.
    }

    /**
     * This method is called when a new track is added to the peer connection.
     *
     * @param rtpReceiver  The receiver for the new track.
     * @param mediaStreams The media streams containing the new track.
     */
    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        // No action needed in this implementation.
    }
}