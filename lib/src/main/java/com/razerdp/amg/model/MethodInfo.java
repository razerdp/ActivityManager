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
    final boolean hasBundle;

    public MethodInfo(String methodName, Class<? extends Activity> activityClass) {
        this(methodName, activityClass, false);
    }

    public MethodInfo(String methodName, Class<? extends Activity> activityClass, boolean hasBundle) {
        this.methodName = methodName;
        this.activityClass = activityClass;
        this.hasBundle = hasBundle;
    }
}
