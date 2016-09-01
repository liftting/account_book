package com.cyg.cygnet.base;

/**
 * Created by wm on 16/8/30.
 */
public interface IParser<T> {

    CtResult<T> parse(String response);

}
