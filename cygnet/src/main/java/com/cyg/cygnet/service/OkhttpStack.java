package com.cyg.cygnet.service;

import android.content.Context;
import android.os.Handler;

import com.cyg.cygnet.base.AbsHttpStack;
import com.cyg.cygnet.base.CtRequestParams;
import com.cyg.cygnet.base.FileWrapper;
import com.cyg.cygnet.base.ICallBack;
import com.cyg.cygnet.base.IParser;
import com.cyg.cygnet.base.Part;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wm on 16/8/30.
 */
public class OkhttpStack<T> extends AbsHttpStack<T> {

    private OkHttpClient mClient;
    private Handler mHandler;

    public static OkhttpStack instance;

    public static OkhttpStack getInstance(Context context) {
        if (instance == null) {
            instance = new OkhttpStack(context);
        }
        return instance;
    }

    private OkhttpStack(Context context) {
        mClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new CacheInterceptor())//缓存拦截器
                .addNetworkInterceptor(new LogInterceptor())
                .cache(new CacheProvide(context).provideCache())//缓存空间提供器
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        mHandler = new Handler();// 这个是主线程的handler
    }


    @Override
    public void get(String url, IParser<T> parser, ICallBack<T> callback, Object tag) {
        Request request = new Request.Builder().url(url).build();
        exec(request, parser, callback, tag);
    }

    @Override
    public void post(String url, CtRequestParams params, IParser<T> parser, ICallBack<T> callback, Object tag) {
        Request request = new Request.Builder().post(getRequestBody(params)).build();
        exec(request, parser, callback, tag);

    }

    @Override
    public void post(String url, String json, IParser<T> parser, ICallBack<T> callback, Object tag) {
        Request request = new Request.Builder().post(getJsonRequestBody(json)).build();
        exec(request, parser, callback, tag);
    }

    @Override
    public void put(String url, CtRequestParams params, IParser<T> parser, ICallBack<T> callback, Object tag) {

    }

    @Override
    public void put(String url, String json, IParser<T> parser, ICallBack<T> callback, Object tag) {

    }

    @Override
    public void delete(String url, IParser<T> parser, ICallBack<T> callback, Object tag) {

    }

    @Override
    public void cancel(Object tag) {

    }

    @Override
    public LinkedHashMap<String, String> headers() {
        return null;
    }

    /**
     * 文件， form 表单提交
     *
     * @param params
     * @return
     */
    private RequestBody getRequestBody(CtRequestParams params) {
        // fileBody
        RequestBody body = null;
        if (params.getFiles().size() > 0) {
            boolean hasData = false;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Part part : params.getParams()) {
                String key = part.getKey();
                String value = part.getValue();
                builder.addFormDataPart(key, value);
                hasData = true;
            }

            for (Part part : params.getFiles()) {
                String key = part.getKey();
                FileWrapper file = part.getFileWrapper();
                if (file != null) {
                    hasData = true;
                    builder.addFormDataPart(key, file.getFileName(), RequestBody.create(file.getMediaType(), file.getFile()));
                }
            }
            if (hasData) {
                body = builder.build();
            }
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Part part : params.getParams()) {
                String key = part.getKey();
                String value = part.getValue();
                builder.add(key, value);
            }
            body = builder.build();
        }

        return body;
    }

    private RequestBody getJsonRequestBody(String json) {
        RequestBody body = RequestBody.create(MediaType
                .parse("application/json;charset=utf-8"), json);
        return body;
    }

    private void exec(Request request, final IParser<T> parser, final ICallBack<T> callBack, Object tag) {
        Request.Builder builder = new Request.Builder();
        builder.tag(tag);

        //为了再一次平装头部，
        LinkedHashMap<String, String> map = headers();
        if (map != null && !map.isEmpty()) {
            for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                String value = map.get(key);
                if (value == null) continue;
                builder.addHeader(key, value);
            }
            request = builder.build();
        }

        //可以在处理
        final Call call = mClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String msg = e.getMessage();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(callBack, msg);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //处理回调这块要处理
                int code = response.code();//状态编码
                final String resp = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onNetResponse(parser, callBack, resp);
                    }
                });
            }
        });

    }
}
