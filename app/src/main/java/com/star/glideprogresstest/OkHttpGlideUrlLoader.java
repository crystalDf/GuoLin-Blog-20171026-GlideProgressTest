package com.star.glideprogresstest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class OkHttpGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private OkHttpClient mOkHttpClient;

    public OkHttpGlideUrlLoader(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl glideUrl, int width, int height, @NonNull Options options) {
        return new LoadData<>(glideUrl, new OkHttpFetcher(mOkHttpClient, glideUrl));
    }

    @Override
    public boolean handles(@NonNull GlideUrl glideUrl) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {

        private OkHttpClient mOkHttpClient;

        public Factory() {
        }

        public Factory(OkHttpClient okHttpClient) {
            mOkHttpClient = okHttpClient;
        }

        public synchronized OkHttpClient getOkHttpClient() {

            if (mOkHttpClient == null) {
                mOkHttpClient = new OkHttpClient();
            }

            return mOkHttpClient;
        }

        @NonNull
        @Override
        public ModelLoader<GlideUrl, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new OkHttpGlideUrlLoader(getOkHttpClient());
        }

        @Override
        public void teardown() {

        }
    }
}
