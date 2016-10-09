package com.sunlianglong.broadcastbestpractice;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun liang long on 2016/8/12.
 */
public class ActivityCollector {
    public static List<Activity> activities =new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
