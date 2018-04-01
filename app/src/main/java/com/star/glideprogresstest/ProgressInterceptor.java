package com.star.glideprogresstest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProgressInterceptor implements Interceptor {

    private static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    public static Map<String, ProgressListener> getListenerMap() {
        return LISTENER_MAP;
    }

    public static void addListener(String url, ProgressListener progressListener) {
        LISTENER_MAP.put(url, progressListener);
    }

    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        ResponseBody responseBody = response.body();

        Response newResponse = response.newBuilder().body(
                new ProgressResponseBody(url, responseBody)).build();

        return newResponse;
    }
}
