package com.zhongyi.mind.util;

import android.util.Log;

import com.zhongyi.mind.BuildConfig;



/**
 * Created by 15342 on 2018/4/15.
 */

public class LogUtils {
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    private LogUtils(){
        /* Protect from instantiations */
    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog( String log ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements){
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }


    public static void e(String message){
        if (!isDebuggable())
            return;

        // Throwable instance must be created before any methods
        getMethodNames(Thread.currentThread().getStackTrace());
        Log.e(className, createLog(message));
    }


    public static void i(String message){
        if (!isDebuggable())
            return;

        getMethodNames(Thread.currentThread().getStackTrace());
        Log.i(className, createLog(message));
    }

    public static void d(String message){
        if (!isDebuggable())
            return;

        getMethodNames(Thread.currentThread().getStackTrace());
        Log.d(className, createLog(message));
    }

    public static void v(String message){
        if (!isDebuggable())
            return;

        getMethodNames(Thread.currentThread().getStackTrace());
        Log.v(className, createLog(message));
    }

    public static void w(String message){
        if (!isDebuggable())
            return;

        getMethodNames(Thread.currentThread().getStackTrace());
        Log.w(className, createLog(message));
    }

    public static void wtf(String message){
        if (!isDebuggable())
            return;

        getMethodNames(Thread.currentThread().getStackTrace());
        Log.wtf(className, createLog(message));
    }
}
