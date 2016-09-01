package com.cyg.cygnet.base;

import android.text.TextUtils;

/**
 * Created by wm on 16/8/30.
 */
public class Part {

    private String key;
    private String value;
    private FileWrapper fileWrapper;

    public Part(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public Part(String key, FileWrapper fileWrapper) {
        setKey(key);
        this.fileWrapper = fileWrapper;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public FileWrapper getFileWrapper() {
        return fileWrapper;
    }

    protected void setKey(String key) {
        if (key == null) {
            this.key = "";
        } else {
            this.key = key;
        }
    }

    protected void setValue(String value) {
        if (value == null) {
            this.value = "";
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Part)) {
            return false;
        }
        Part part = (Part) o;
        if (part == null) {
            return false;
        }
        if (TextUtils.equals(part.getKey(), getKey()) && TextUtils.equals(part.getValue(), getValue())) {
            return true;
        }
        return false;


    }
}
