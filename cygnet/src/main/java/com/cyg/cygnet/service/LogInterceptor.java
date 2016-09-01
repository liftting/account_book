package com.cyg.cygnet.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wm on 16/8/31.
 */
public class LogInterceptor implements Interceptor {

    private static final String TAG = "CygNet";

    @Override
    public Response intercept(Chain chain) throws IOException {
        long t1 = System.nanoTime();

        Request request = chain.request();
        Log.d(TAG, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));


        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.d(TAG, String.format("Received response for %s in %.1fms%n%s\\n%s",
                request.url(), (t2 - t1) / 1e6d, response.headers(), "response-code:" + response.code()));

        return response;
    }
}
