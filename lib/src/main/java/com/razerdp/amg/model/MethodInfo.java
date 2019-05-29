package com.razerdp.amg.model;

import android.app.Activity;
import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public class MethodInfo {
    Method method;
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
        findMethod();
    }

    private void findMethod() {
        if (method != null) return;
        try {
            if (hasBundle) {
                this.method = activityClass.getMethod(methodName, Bundle.class);
            } else {
                this.method = activityClass.getMethod(methodName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invoke(Activity act, Bundle bundle) {
        findMethod();
        try {
            if (method != null) {
                if (hasBundle) {
                    method.invoke(act, bundle);
                } else {
                    method.invoke(act);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
