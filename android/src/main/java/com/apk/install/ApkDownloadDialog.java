package com.apk.install;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ApkDownloadDialog extends AlertDialog {
    private ProgressBar mProgress;
    private TextView mProgressMessage;
    private Handler mViewUpdateHandler;
    private int mMax;
    private boolean mHasStarted;
    private int mProgressVal;

    public ApkDownloadDialog(Context context) {
        super(context);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_bar);
        mProgress = (ProgressBar) findViewById(R.id.activity_pdfload_progressBar);
        mProgressMessage = (TextView) findViewById(R.id.activity_pdfload_tvProgress);
        mViewUpdateHandler = new Handler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                float max = mProgress.getMax();
                float cur = mProgress.getProgress();
                int rate = (int) (cur * 100 / max);
                mProgressMessage.setText(rate + "%");
            }
        };
        onProgressChanged();
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
    }

    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }


    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }
}
