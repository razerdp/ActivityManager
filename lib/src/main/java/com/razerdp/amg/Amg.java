package com.razerdp.amg;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */
public class Amg {
    private static final String TAG = "Amg";

    private static boolean DEBUG = false;
    private int activityIndex = 0;
    private static final HashMap<Class<? extends Activity>, SparseArray<ActivityInfo>> mActivityMaps = new HashMap<>();

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
                activityIndex++;
                putIntoMap(activity);
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
                activityIndex--;
                removeFromMap(activity);
            }
        });
    }

    private void putIntoMap(Activity act) {
        if (act == null) return;
        final int hashCode = act.hashCode();
        Class<? extends Activity> actClass = act.getClass();
        SparseArray<ActivityInfo> acts = mActivityMaps.get(actClass);
        if (acts == null) {
            acts = new SparseArray<>();
            mActivityMaps.put(actClass, acts);
        }
        if (acts.get(hashCode) != null) return;
        acts.append(hashCode, new ActivityInfo(act, activityIndex));
    }

    private void removeFromMap(Activity act) {
        if (act == null) return;
        final int hashCode = act.hashCode();
        Class<? extends Activity> actClass = act.getClass();

        SparseArray<ActivityInfo> acts = mActivityMaps.get(actClass);
        if (acts == null) return;

        ActivityInfo activityInfo = acts.get(hashCode);
        if (activityInfo == null) return;
        try {
            activityInfo.act.clear();
            acts.remove(hashCode);
        } catch (Exception e) {

        }
    }


//    public void close(Class<? extends Activity>... classes) {
//        close(true, classes);
//    }
//
//    public void close(boolean closeAll, Class<? extends Activity>... classes) {
//        if (classes == null) return;
//        for (Class<? extends Activity> aClass : classes) {
//
//        }
//    }


    private class ActivityInfo {
        final WeakReference<Activity> act;
        final int index;

        ActivityInfo(Activity activity, int index) {
            this.act = new WeakReference<>(activity);
            this.index = index;
        }
    }
}
