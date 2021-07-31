package com.wdl.rehost.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

import static com.qihoo360.loader2.PMF.getApplicationContext;

/**
 * @Date: 2021/7/31
 * @author: WuDongLin
 * @Description:
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 下载文件
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public static void downLoadFile(String fileUrl, final String destFileDir, final ReqProgressCallBack callBack) {
        final String fileName = "plugin1.apk";
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e(TAG, e.toString());
                callBack.onFail("下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                java.io.InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    android.util.Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        android.util.Log.e(TAG, "current------>" + current);
                        callBack.onProgress(total, current);
                    }
                    fos.flush();
                    callBack.successCallBack(file.getAbsolutePath());
                } catch (IOException e) {
                    android.util.Log.e(TAG, e.toString());
                    callBack.onFail("下载失败");
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        android.util.Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    ReqProgressCallBack reqProgressCallBack;


    public void setReqProgressCallBack(ReqProgressCallBack reqProgressCallBack) {
        this.reqProgressCallBack = reqProgressCallBack;
    }

    public interface ReqProgressCallBack {
        /**
         * 响应进度更新
         */
        void onProgress(long total, long current);

        void successCallBack(String path);

        void onFail(String s);
    }

    public static String MD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
