package com.cyg.cygnet.base;

/**
 * Created by wm on 16/8/30.
 */
public class CtExecManager {

    private static ICtNetStack mNetStack;

    public static void init(ICtNetStack stack) {
        mNetStack = stack;
    }

    public static <T> void get(final String url, final IParser<T> parser,
                               final ICallBack<T> callback, final Object tag) {
        try {
            mNetStack.get(url, parser, callback, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void post(final String url, final CtRequestParams params,
                                final IParser<T> parser, final ICallBack<T> callback, final Object tag) {
        try {
            mNetStack.post(url, params, parser, callback, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
