package com.razerdp.amg.model;

import android.app.Activity;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public class MethodInfo {
    final String methodName;
    final Class<? extends Activity> activityClass;
    final int priority;

    public MethodInfo(String methodName, Class<? extends Activity> activityClass) {
        this(methodName, activityClass, 0);
    }

    public MethodInfo(String methodName, Class<? extends Activity> activityClass, int priority) {
        this.methodName = methodName;
        this.activityClass = activityClass;
        this.priority = priority;
    }
}
