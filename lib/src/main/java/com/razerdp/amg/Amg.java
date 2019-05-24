package com.razerdp.amg;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public class Amg {
    private static final String TAG = "Amg";

    private static boolean DEBUG = false;

    private static class SingleTonHolder {
        private static Amg INSTANCE = new Amg();
    }

    private Amg() {

    }

    public static Amg getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    void init(Context context) {
        regLifeCallback((Application) context);
    }

    private void regLifeCallback(Application context) {
        context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
