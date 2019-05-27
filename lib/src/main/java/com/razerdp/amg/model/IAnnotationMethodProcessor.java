package com.razerdp.amg.model;

import android.app.Activity;

import java.lang.reflect.Method;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public interface IAnnotationMethodProcessor {
    Method getMethodInfo(Class<? extends Activity> actClass);
}
