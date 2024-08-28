package com.example.internapp.VideoCall;

import static android.content.ContentValues.TAG;

import static com.example.internapp.VideoCall.Credentials.TURN_PASSWORD;
import static com.example.internapp.VideoCall.Credentials.TURN_USERNAME;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for WebRTC functionality.
 */
public class WebRTCClient {

    private final Gson gson = new Gson();
    private final Context context;
    private final String username;
    private final EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
    private final PeerConnectionFactory peerConnectionFactory;
    private final PeerConnection peerConnection;
    private final List<PeerConnection.IceServer> iceServer = new ArrayList<>();
    private final VideoSource localVideoSource;
    private final AudioSource localAudioSource;
    private final MediaConstraints mediaConstraints = new MediaConstraints();
    public Listener listener;
    private CameraVideoCapturer videoCapturer;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;

    /**
     * Constructor for WebRTCClient.
     *
     * @param context  The application context.
     * @param observer The observer for PeerConnection.
     * @param username The username.
     */
    public WebRTCClient(Context context, PeerConnection.Observer observer, String username) {
        this.context = context;
        this.username = username;
        initPeerConnectionFactory();
        peerConnectionFactory = createPeerConnectionFactory();
        iceServer.add(PeerConnection.IceServer.builder("stun:stun.relay.metered.ca:80").createIceServer());
        iceServer.add(PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80")
                .setUsername(TURN_USERNAME)
                .setPassword(TURN_PASSWORD).createIceServer());
        iceServer.add(PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
                .setUsername(TURN_USERNAME)
                .setPassword(TURN_PASSWORD).createIceServer());
        iceServer.add(PeerConnection.IceServer.builder("turn:global.relay.metered.ca:443")
                .setUsername(TURN_USERNAME)
                .setPassword(TURN_PASSWORD).createIceServer());
        iceServer.add(PeerConnection.IceServer.builder("turns:global.relay.metered.ca:443?transport=tcp")
                .setUsername(TURN_USERNAME)
                .setPassword(TURN_PASSWORD).createIceServer());
        peerConnection = createPeerConnection(observer);
        localVideoSource = peerConnectionFactory.createVideoSource(false);
        localAudioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
    }

    // Initializing peer connection section

    /**
     * Initializes the PeerConnectionFactory.
     */
    private void initPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(context).setFieldTrials("WebRTC-H264HighProfile/Enabled/").setEnableInternalTracer(true).createInitializationOptions();
        PeerConnectionFactory.initialize(options);
    }

    /**
     * Creates a PeerConnectionFactory instance.
     *
     * @return The created PeerConnectionFactory.
     */
    private PeerConnectionFactory createPeerConnectionFactory() {
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption = false;
        options.disableNetworkMonitor = false;
        return PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setOptions(options).createPeerConnectionFactory();
    }

    /**
     * Creates a PeerConnection instance.
     *
     * @param observer The observer for the PeerConnection.
     * @return The created PeerConnection.
     */
    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
        return peerConnectionFactory.createPeerConnection(iceServer, observer);
    }

    // Initializing UI like SurfaceViewRenderers

    /**
     * Initializes a SurfaceViewRenderer for local video display.
     *
     * @param viewRenderer The SurfaceViewRenderer to initialize.
     */
    public void initSurfaceViewRenderer(SurfaceViewRenderer viewRenderer) {
        viewRenderer.setEnableHardwareScaler(true);
        viewRenderer.setMirror(true);
        viewRenderer.init(eglBaseContext, null);
    }

    /**
     * Initializes local video SurfaceViewRenderer and starts video streaming.
     *
     * @param view The SurfaceViewRenderer for local video display.
     */

    public void initLocalSurfaceView(SurfaceViewRenderer view) {
        view.setZOrderMediaOverlay(true);
        view.setClipToOutline(true);
        initSurfaceViewRenderer(view);

        view.setOnTouchListener(new View.OnTouchListener() {
            private int lastX, lastY;
            private int initialX, initialY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDragging = true;
                        initialX = (int) event.getRawX();
                        initialY = (int) event.getRawY();
                        lastX = initialX;
                        lastY = initialY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            int dx = (int) event.getRawX() - lastX;
                            int dy = (int) event.getRawY() - lastY;
                            int newX = v.getLeft() + dx;
                            int newY = v.getTop() + dy;
                            v.layout(newX, newY, newX + v.getWidth(), newY + v.getHeight());
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        break;
                }
                return true;
            }
        });

        startLocalVideoStreaming(view);
    }

    /**
     * Starts local video streaming to the provided SurfaceViewRenderer.
     *
     * @param view The SurfaceViewRenderer for local video display.
     */
    private void startLocalVideoStreaming(SurfaceViewRenderer view) {
        SurfaceTextureHelper helper = SurfaceTextureHelper.create(
                Thread.currentThread().getName(), eglBaseContext
        );

        videoCapturer = getVideoCapturer();
        videoCapturer.initialize(helper, context, localVideoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 360, 15);
        String localTrackId = "local_track";
        localVideoTrack = peerConnectionFactory.createVideoTrack(
                localTrackId + "_video", localVideoSource
        );
        localVideoTrack.addSink(view);


        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId + "_audio", localAudioSource);
        String localStreamId = "local_stream";
        MediaStream localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
        localStream.addTrack(localVideoTrack);
        localStream.addTrack(localAudioTrack);
        peerConnection.addStream(localStream);
    }

    /**
     * Retrieves the front-facing camera video capturer.
     *
     * @return The front-facing camera video capturer.
     */
    private CameraVideoCapturer getVideoCapturer() {
        Camera2Enumerator enumerator = new Camera2Enumerator(context);

        String[] deviceNames = enumerator.getDeviceNames();

        for (String device : deviceNames) {
            if (enumerator.isFrontFacing(device)) {
                return enumerator.createCapturer(device, null);
            }
        }
        throw new IllegalStateException("front facing camera not found");
    }

    /**
     * Initializes a SurfaceViewRenderer for remote video display.
     *
     * @param view The SurfaceViewRenderer to initialize.
     */
    public void initRemoteSurfaceView(SurfaceViewRenderer view) {
        initSurfaceViewRenderer(view);
    }

    // Negotiation section like call and answer

    /**
     * Initiates a call.
     *
     * @param target The target to call.
     */
    public void call(String target) {
        try {
            peerConnection.createOffer(new MySdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new MySdpObserver() {
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            if (listener != null) {
                                listener.onTransferDataToOtherPeer(new DataModel(
                                        target, username, sessionDescription.description, DataModelType.Offer
                                ));
                            }
                        }
                    }, sessionDescription);
                }
            }, mediaConstraints);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Answers an incoming call.
     *
     * @param target The target to answer.
     */
    public void answer(String target) {
        try {
            peerConnection.createAnswer(new MySdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new MySdpObserver() {
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            if (listener != null) {
                                listener.onTransferDataToOtherPeer(new DataModel(
                                        target, username, sessionDescription.description, DataModelType.Answer
                                ));
                            }
                        }
                    }, sessionDescription);
                }
            }, mediaConstraints);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Receives a session description from the remote peer.
     *
     * @param sessionDescription The received session description.
     */
    public void onRemoteSessionReceived(SessionDescription sessionDescription) {
        peerConnection.setRemoteDescription(new MySdpObserver(), sessionDescription);
    }

    /**
     * Adds an ICE candidate to the peer connection.
     *
     * @param iceCandidate The ICE candidate to add.
     */
    public void addIceCandidate(IceCandidate iceCandidate) {
        peerConnection.addIceCandidate(iceCandidate);
    }

    /**
     * Sends an ICE candidate to the remote peer.
     *
     * @param iceCandidate The ICE candidate to send.
     * @param target       The target to send to.
     */
    public void sendIceCandidate(IceCandidate iceCandidate, String target) {
        addIceCandidate(iceCandidate);
        if (listener != null) {
            listener.onTransferDataToOtherPeer(new DataModel(
                    target, username, gson.toJson(iceCandidate), DataModelType.IceCandidate
            ));
        }
    }

    /**
     * Switches the audio device.
     *
     * @param shouldBeSpeaker Whether to switch to speaker or not.
     */
    public void switchAudioDevice(Boolean shouldBeSpeaker) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(shouldBeSpeaker);
    }

    /**
     * Switches the camera.
     */
    public void switchCamera() {
        videoCapturer.switchCamera(null);
    }

    /**
     * Toggles the local video track.
     *
     * @param shouldBeMuted Whether the video should be muted or not.
     */
    public void toggleVideo(Boolean shouldBeMuted) {
        localVideoTrack.setEnabled(shouldBeMuted);
    }

    /**
     * Toggles the local audio track.
     *
     * @param shouldBeMuted Whether the audio should be muted or not.
     */
    public void toggleAudio(Boolean shouldBeMuted) {
        localAudioTrack.setEnabled(shouldBeMuted);
    }

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        try {

            localVideoTrack.dispose();
            videoCapturer.stopCapture();
            videoCapturer.dispose();
            peerConnection.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Listener interface for WebRTCClient events.
     */
    public interface Listener {
        void onTransferDataToOtherPeer(DataModel model);
    }
}