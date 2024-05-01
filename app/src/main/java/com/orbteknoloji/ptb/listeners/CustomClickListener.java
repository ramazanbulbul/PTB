package com.orbteknoloji.ptb.listeners;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;


public abstract class CustomClickListener implements View.OnClickListener {
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private boolean isSingleEvent;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private Handler handler;
    private Runnable runnable;
    private View _v;
    private int _position;
    public CustomClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isSingleEvent) {
                    onSingleClick(_v, _position);
                }
            }
        };
    }
    public void onStart(View v, int position){
        _v = v;
        _position = position;
    }
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            isSingleEvent = false;
            handler.removeCallbacks(runnable);
            onDoubleClick(_v, _position);
            return;
        }

        isSingleEvent = true;
        handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN);
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick(View v, int position);
    public abstract void onSingleClick(View v, int position);
}