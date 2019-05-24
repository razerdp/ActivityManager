package com.razerdp.amg.model;

import android.app.Activity;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：for annotation processor
 */
public interface IMethodInfo {

    Class<? extends Activity> getActivityClass();

    MethodInfo[] getMethods();
}
