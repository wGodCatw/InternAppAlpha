package com.example.internapp.VideoCall;// CustomSurfaceViewRenderer.java

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

import org.webrtc.SurfaceViewRenderer;

public class CustomSurfaceViewRenderer extends SurfaceViewRenderer {

    public CustomSurfaceViewRenderer(Context context) {
        super(context);
        init();
    }

    public CustomSurfaceViewRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSurfaceViewRenderer(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        init();
    }

    private void init() {
        setOutlineProvider(new RoundOutlineProvider(20)); // Set corner radius here
        setClipToOutline(true);
    }

    // RoundOutlineProvider.java



    public static class RoundOutlineProvider extends ViewOutlineProvider {

        private float radius;

        public RoundOutlineProvider(float radius) {
            this.radius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }
    }
}