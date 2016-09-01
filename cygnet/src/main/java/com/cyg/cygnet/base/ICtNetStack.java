package com.cyg.cygnet.base;

import java.util.LinkedHashMap;

/**
 * Created by wm on 16/8/30.
 */
public interface ICtNetStack<T> {

    void get(final String url, final IParser<T> parser,
             final ICallBack<T> callback, final Object tag);

    void post(final String url, final CtRequestParams params,
              final IParser<T> parser,
              final ICallBack<T> callback,
              final Object tag);

    void post(final String url, final String json,
              final IParser<T> parser,
              final ICallBack<T> callback,
              final Object tag);

    void put(final String url, final CtRequestParams params,
             final IParser<T> parser,
             final ICallBack<T> callback,
             final Object tag);

    void put(final String url, final String json,
             final IParser<T> parser,
             final ICallBack<T> callback,
             final Object tag);

    void delete(final String url, final IParser<T> parser,
                final ICallBack<T> callback, final Object tag);

    void onNetResponse(final IParser<T> parser,
                       final ICallBack<T> callback,
                       final String response);

    void onError(final ICallBack<T> callback, final String msg);

    void cancel(final Object tag);

    LinkedHashMap<String, String> headers();

}
