package com.cyg.cygnet.base.parser;

import com.cyg.cygnet.base.CtResult;
import com.cyg.cygnet.base.IParser;

/**
 * Created by wm on 16/8/30.
 */
public class CtStringParser implements IParser<String> {

    @Override
    public CtResult<String> parse(String response) {
        CtResult<String> result = new CtResult<>();
        result.setResult(response);
        return result;
    }

}
