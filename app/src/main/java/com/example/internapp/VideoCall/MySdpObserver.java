package com.example.internapp.VideoCall;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * MySdpObserver class implements the SdpObserver interface
 * to observe events related to Session Description Protocol (SDP).
 */
public class MySdpObserver implements SdpObserver {

    /**
     * This method is called when SDP creation is successful.
     *
     * @param sessionDescription The created session description.
     */
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when SDP setting is successful.
     */
    @Override
    public void onSetSuccess() {
        // No action needed in this implementation.
    }

    /**
     * This method is called when SDP creation fails.
     *
     * @param error Description of the error occurred during SDP creation.
     */
    @Override
    public void onCreateFailure(String error) {
        // No action needed in this implementation.
    }

    /**
     * This method is called when SDP setting fails.
     *
     * @param error Description of the error occurred during SDP setting.
     */
    @Override
    public void onSetFailure(String error) {
        // No action needed in this implementation.
    }
}