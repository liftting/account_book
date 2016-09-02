package com.cyg.cygnet.okio;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by wm on 16/9/2.
 */
public class OkIoMain {

    public static void main(String[] args) {

        write();

        read();

    }

    public static void write() {
        File file = new File("/Users/wm/res-cyg.txt");
        Sink sink = null;
        BufferedSink bufferedSink = null;

        try {
            sink = Okio.sink(file);
            bufferedSink = Okio.buffer(sink);

            String data = new String("aaa");
            bufferedSink.writeUtf8(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bufferedSink);
        }

    }

    public static void read() {
        File file = new File("/Users/wm/res-cyg.txt");
        Source source = null;
        BufferedSource bufferedSource = null;

        try {
            source = Okio.source(file);
            bufferedSource = Okio.buffer(source);

            String content = bufferedSource.readUtf8();
            System.out.print(content);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bufferedSource);
        }
    }

    public static void closeQuietly(Closeable closeable) {

        if (closeable != null) {

            try {

                closeable.close();

            } catch (RuntimeException rethrown) {

                throw rethrown;

            } catch (Exception ignored) {

            }

        }

    }

}
