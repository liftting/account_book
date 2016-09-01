package com.cyg.cygnet.base;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.MediaType;

/**
 * Created by wm on 16/8/30.
 * 放一些参数进去
 */
public class CtRequestParams {

    private final List<Part> params = new ArrayList<>();
    private final List<Part> files = new ArrayList<>();

    public CtRequestParams() {

    }

    //==================================params====================================

    /**
     * @param key
     * @param value
     */
    public void addFormDataPart(String key, String value) {
        if (value == null) {
            value = "";
        }

        Part part = new Part(key, value);
        if (!TextUtils.isEmpty(key) && !params.contains(part)) {
            params.add(part);
        }
    }

    public void addFormDataPart(String key, int value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, long value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, float value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, double value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, boolean value) {
        addFormDataPart(key, String.valueOf(value));
    }

    /**
     * @param key
     * @param file
     */
    public void addFormDataPart(String key, File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        boolean isPng = file.getName().lastIndexOf("png") > 0 || file.getName().lastIndexOf("PNG") > 0;
        if (isPng) {
            addFormDataPart(key, file, "image/png; charset=UTF-8");
            return;
        }

        boolean isJpg = file.getName().lastIndexOf("jpg") > 0 || file.getName().lastIndexOf("JPG") > 0
                || file.getName().lastIndexOf("jpeg") > 0 || file.getName().lastIndexOf("JPEG") > 0;
        if (isJpg) {
            addFormDataPart(key, file, "image/jpeg; charset=UTF-8");
            return;
        }

        if (!isPng && !isJpg) {
            addFormDataPart(key, new FileWrapper(file, null));
        }
    }

    public void addFormDataPart(String key, File file, String contentType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        MediaType mediaType = null;
        try {
            mediaType = MediaType.parse(contentType);
        } catch (Exception e) {

        }

        addFormDataPart(key, new FileWrapper(file, mediaType));
    }

    public void addFormDataPart(String key, File file, MediaType mediaType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        addFormDataPart(key, new FileWrapper(file, mediaType));
    }


    public void addFormDataPartFiles(String key, List<File> files) {
        for (File file : files) {
            if (file == null || !file.exists() || file.length() == 0) {
                continue;
            }
            addFormDataPart(key, file);
        }
    }

    public void addFormDataPart(String key, List<File> files, MediaType mediaType) {
        for (File file : files) {
            if (file == null || !file.exists() || file.length() == 0) {
                continue;
            }
            addFormDataPart(key, new FileWrapper(file, mediaType));
        }
    }

    public void addFormDataPart(String key, FileWrapper fileWrapper) {
        if (!TextUtils.isEmpty(key) && fileWrapper != null) {
            File file = fileWrapper.getFile();
            if (file == null || !file.exists() || file.length() == 0) {
                return;
            }
            files.add(new Part(key, fileWrapper));
        }
    }

    public void addFormDataPart(String key, List<FileWrapper> fileWrappers) {
        for (FileWrapper fileWrapper : fileWrappers) {
            addFormDataPart(key, fileWrapper);
        }
    }

    public void addFormDataParts(List<Part> params) {
        this.params.addAll(params);
    }

    public List<Part> getFiles() {
        return files;
    }

    public List<Part> getParams() {
        return params;
    }


}
