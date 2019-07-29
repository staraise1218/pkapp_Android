package com.zhongyi.mind.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
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

import static java.lang.String.valueOf;

/**
 * Created by 15342 on 2018/4/15.
 */

public class NetUtils {

    //回调方法
    private static Callback callback;
    //设置回调
    public static void setCallback(Callback callback) {
        NetUtils.callback = callback;
    }

    /**
     * Get请求
     * @param url
     */
    public static void getDataAsync(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Post请求
     */
    public static void postDataWithParame(String url, Map<String,String> requestParams) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        //设置请求参数
        Set<String> keyset = requestParams.keySet();
        if (keyset!=null&&keyset.size()>0){
            for (String key:keyset){
                formBody.add(key,requestParams.get(key));//传递键值对参数
            }
        }
        //设置请求体对象
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(callback);//回调方法的使用与get异步请求相同，此时略。
    }

    //参数为要上传的网址，本地照片在本地的地址，我们自己定义的接口
    public static void post_file(final String url, final Map<String, String> map, File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            RequestBody body;
            // MediaType.parse() 里面是上传的文件类型。
            String flag = map.get("flag");
            if ("pic".equals(flag)){
                body = RequestBody.create(MediaType.parse("image/*"), file);
            }else{
                body = RequestBody.create(MediaType.parse("video/*"), file);
            }
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        //设置请求体对象
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(requestBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(callback);//回调方法的使用与get异步请求相同，此时略。
    }
}

