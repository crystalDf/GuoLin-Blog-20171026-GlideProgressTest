package com.star.glideprogresstest;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private static final String TAG = "ProgressResponseBody";

    private BufferedSource mBufferedSource;
    private ResponseBody mResponseBody;
    private ProgressListener mProgressListener;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        mResponseBody = responseBody;
        mProgressListener = ProgressInterceptor.getListenerMap().get(url);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(new ProgressSource(mResponseBody.source()));
        }

        return mBufferedSource;
    }

    private class ProgressSource extends ForwardingSource {

        private long mTotalBytesRead;
        private int mCurrentProgress;

        public ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {

            long bytesRead = super.read(sink, byteCount);
            long fullLength = mResponseBody.contentLength();

            if (bytesRead == -1) {
                mTotalBytesRead = fullLength;
            } else {
                mTotalBytesRead += bytesRead;
            }

            int progress = (int) (100f * mTotalBytesRead / fullLength);

            Log.d(TAG, "download progress is " + progress);

            if ((mProgressListener != null) && (progress != mCurrentProgress)) {
                mProgressListener.onProgress(progress);
            }

            if ((mProgressListener != null) && (mTotalBytesRead == fullLength)) {
                mProgressListener = null;
            }

            mCurrentProgress = progress;

            return bytesRead;
        }
    }
}
