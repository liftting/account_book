package com.cyg.cygnet.base;

/**
 * Created by qibin on 2015/11/29.
 */
public abstract class AbsHttpStack<T> implements ICtNetStack<T> {

    protected boolean debug;

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebugMode() {
        return this.debug;
    }

    @Override
    public void onNetResponse(IParser<T> parser, ICallBack<T> callback, String response) {
        if (isDebugMode()) System.out.println(response);
        if (callback == null) return;
        if (parser == null) {
            CtResult<T> result = new CtResult<T>();
            result.setStatus(CtResult.ERROR);
            callback.callback(result);
            return;
        }

        CtResult<T> result = parser.parse(response);
        callback.callback(result);
    }

    @Override
    public void onError(ICallBack<T> callback, String msg) {
        if (isDebugMode()) System.out.println(msg);
        if (callback == null) return;

        CtResult<T> result = new CtResult<T>();
        result.setStatus(CtResult.ERROR);
        result.setMsg(msg);
        callback.callback(result);
    }
}
