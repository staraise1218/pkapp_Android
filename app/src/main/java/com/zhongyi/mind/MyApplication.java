package com.zhongyi.mind;

import android.app.Activity;
import android.app.Application;

import com.zhongyi.mind.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {
    List<Activity> activities = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils.intiContext(this);
    }

    //存放activity
    public void setActivities(Activity activity){
        activities.add(activity);
    }

    //移除activity
    public void destory(){
        if (activities.size()>0){
            for (Activity activity:activities){
                activity.finish();
            }
        }
    }
}
