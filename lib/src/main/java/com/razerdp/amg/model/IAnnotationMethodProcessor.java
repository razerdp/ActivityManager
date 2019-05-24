package com.razerdp.amg.model;

import android.app.Activity;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public interface IAnnotationMethodProcessor {
    IMethodInfo getMethodInfo(Class<? extends Activity> actClass);
}
