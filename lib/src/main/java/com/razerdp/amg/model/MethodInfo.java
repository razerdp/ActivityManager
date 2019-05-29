package com.razerdp.amg.model;

import android.app.Activity;

import java.lang.reflect.Method;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public class MethodInfo {
    final Method method;
    final Class<? extends Activity> activityClass;
    final boolean hasBundle;

    public MethodInfo(Method method, Class<? extends Activity> activityClass) {
        this(method, activityClass, false);
    }

    public MethodInfo(Method method, Class<? extends Activity> activityClass, boolean hasBundle) {
        this.method = method;
        this.activityClass = activityClass;
        this.hasBundle = hasBundle;
    }
}
