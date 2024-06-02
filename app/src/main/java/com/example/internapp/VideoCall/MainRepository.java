package com.example.internapp.VideoCall;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

/**
 * MainRepository class handles the communication between the application and Firebase/WebRTC clients.
 */
public class MainRepository implements WebRTCClient.Listener {

    /**
     * WebRTC client for establishing and managing WebRTC connections.
     */

    private static MainRepository instance;
    /**
     * Gson instance for JSON serialization/deserialization.
     */
    private final Gson gson = new Gson();
    /**
     * Firebase client for handling authentication and messaging.
     */
    private final FirebaseClient firebaseClient;
    /**
     * Listener for WebRTC events.
     */
    public Listener listener;
    private WebRTCClient webRTCClient;
    /**
     * Username of the current user.
     */
    public String currentUsername;
    /**
     * SurfaceViewRenderer for remote video display.
     */
    private SurfaceViewRenderer remoteView;
    /**
     * Target user for communication.
     */
    private String target;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private MainRepository() {
        this.firebaseClient = new FirebaseClient();
    }

    /**
     * Retrieves the singleton instance of MainRepository.
     *
     * @return MainRepository instance.
     */
    public static MainRepository getInstance() {
        if (instance == null) {
            instance = new MainRepository();
        }
        return instance;
    }

    /**
     * Updates the current username of the user.
     *
     * @param username The new username to be set as the current username.
     */
    private void updateCurrentUsername(String username) {
        this.currentUsername = username;
    }

    /**
     * Logs in the user.
     *
     * @param username Username of the user.
     * @param context  Application context.
     * @param callBack Callback for login success.
     */
    public void login(String username, Context context, SuccessCallback callBack) {
        firebaseClient.login(username, () -> {
            updateCurrentUsername(username);
            this.webRTCClient = new WebRTCClient(context, new MyPeerConnectionObserver() {
                @Override
                public void onAddStream(MediaStream mediaStream) {
                    super.onAddStream(mediaStream);
                    try {
                        mediaStream.videoTracks.get(0).addSink(remoteView);
                    } catch (Exception e) {
                        Log.e("TAG", "onAddStream: ", e);
                    }
                }

                @Override
                public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                    Log.d("TAG", "onConnectionChange: " + newState);
                    super.onConnectionChange(newState);
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED && listener != null) {
                        listener.webrtcConnected();
                    }

                    if (newState == PeerConnection.PeerConnectionState.CLOSED || newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
                        if (listener != null) {
                            listener.webrtcClosed();
                        }
                    }
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {
                    super.onIceCandidate(iceCandidate);
                    webRTCClient.sendIceCandidate(iceCandidate, target);
                }
            }, username);
            webRTCClient.listener = this;
            callBack.onSuccess();
        });
    }

    /**
     * Initializes local video view.
     *
     * @param view SurfaceViewRenderer for local video display.
     */
    public void initLocalView(SurfaceViewRenderer view) {
        webRTCClient.initLocalSurfaceView(view);
    }

    /**
     * Initializes remote video view.
     *
     * @param view SurfaceViewRenderer for remote video display.
     */
    public void initRemoteView(SurfaceViewRenderer view) {
        webRTCClient.initRemoteSurfaceView(view);
        this.remoteView = view;
    }

    /**
     * Initiates a call to the target user.
     *
     * @param target Username of the target user.
     */
    public void startCall(String target) {
        webRTCClient.call(target);
    }

    /**
     * Switches the camera between front and back.
     */
    public void switchCamera() {
        webRTCClient.switchCamera();
    }

    /**
     * Toggles the audio output device.
     *
     * @param shouldBeSpeaker True if speaker should be enabled, false otherwise.
     */
    public void toggleSpeaker(Boolean shouldBeSpeaker) {
        webRTCClient.switchAudioDevice(shouldBeSpeaker);
    }

    /**
     * Toggles the audio mute state.
     *
     * @param shouldBeMuted True if audio should be muted, false otherwise.
     */
    public void toggleAudio(Boolean shouldBeMuted) {
        webRTCClient.toggleAudio(shouldBeMuted);
    }

    /**
     * Toggles the video mute state.
     *
     * @param shouldBeMuted True if video should be muted, false otherwise.
     */
    public void toggleVideo(Boolean shouldBeMuted) {
        webRTCClient.toggleVideo(shouldBeMuted);
    }

    /**
     * Sends a call request to the target user.
     *
     * @param target        Username of the target user.
     * @param errorCallBack Callback for error handling.
     */
    public void sendCallRequest(String target, ErrorCallback errorCallBack) {
        firebaseClient.sendMessageToOtherUser(new DataModel(target, currentUsername, null, DataModelType.StartCall), errorCallBack);
    }

    /**
     * Ends an ongoing call.
     */
    public void endCall() {
        webRTCClient.closeConnection();
    }

    /**
     * Subscribes to the latest event from Firebase.
     *
     * @param callBack Callback for handling new events.
     */
    public void subscribeForLatestEvent(NewEventCallback callBack) {
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()) {

                case Offer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(SessionDescription.Type.OFFER, model.getData()));
                    webRTCClient.answer(model.getSender());
                    break;
                case Answer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(SessionDescription.Type.ANSWER, model.getData()));
                    break;
                case IceCandidate:
                    try {
                        IceCandidate candidate = gson.fromJson(model.getData(), IceCandidate.class);
                        webRTCClient.addIceCandidate(candidate);
                    } catch (Exception e) {
                        Log.e("TAG", "subscribeForLatestEvent: ", e);
                    }
                    break;
                case StartCall:
                    this.target = model.getSender();
                    callBack.onNewEventReceived(model);
                    break;
            }

        });
    }

    /**
     * Callback method for transferring data to other peers.
     *
     * @param model DataModel object to be sent.
     */
    @Override
    public void onTransferDataToOtherPeer(DataModel model) {
        firebaseClient.sendMessageToOtherUser(model, () -> {
        });
    }

    /**
     * Interface for listening to WebRTC events.
     */
    public interface Listener {
        /**
         * Invoked when WebRTC connection is established.
         */
        void webrtcConnected();

        /**
         * Invoked when WebRTC connection is closed.
         */
        void webrtcClosed();
    }
}