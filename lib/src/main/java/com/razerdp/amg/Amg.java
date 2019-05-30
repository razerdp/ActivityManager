package com.razerdp.amg;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.razerdp.amg.annotation.BeforeClose;
import com.razerdp.amg.model.IAnnotationMethodProcessor;
import com.razerdp.amg.model.MethodInfo;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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
    private IAnnotationMethodProcessor methodProcessor;

    private static class SingleTonHolder {
        private static Amg INSTANCE = new Amg();
    }

    private Amg() {

    }

    public static Amg getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    void init(Context context) {
        try {
            this.methodProcessor = (IAnnotationMethodProcessor) Class.forName("com.razerdp.amg.Amg$Inject").newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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


    public void finish(Class<? extends Activity>... classes) {
        FinishBuilder builder = new FinishBuilder();
        for (Class<? extends Activity> aClass : classes) {
            builder.append(aClass, null);
        }
        builder.finish();
    }

    public FinishBuilder multiFinish() {
        return new FinishBuilder();
    }

    void process(FinishBuilder builder) {
        Map<Class<? extends Activity>, Pair<Class<? extends Activity>, Bundle>> map = builder.mClosedMap;
        if (map.isEmpty()) return;
        for (HashMap.Entry<Class<? extends Activity>, Pair<Class<? extends Activity>, Bundle>> entry : map.entrySet()) {
            SparseArray<ActivityInfo> infos = mActivityMaps.get(entry.getKey());
            if (infos.size() <= 0) continue;
            for (int i = 0; i < infos.size(); i++) {
                ActivityInfo activityInfo = infos.valueAt(i);
                processInternal(activityInfo, entry.getValue());
            }
        }

    }

    private void processInternal(ActivityInfo activityInfo, Pair<Class<? extends Activity>, Bundle> value) {
        if (activityInfo == null ||
                activityInfo.act.get() == null ||
                activityInfo.act.get().isDestroyed() ||
                activityInfo.act.get().isFinishing()) {
            return;
        }
        if (methodProcessor == null) {
            doOnReflect(activityInfo, value);
        } else {
            doOnProcessor(activityInfo, value);
        }
        activityInfo.act.get().finish();
    }

    private void doOnReflect(ActivityInfo activityInfo, Pair<Class<? extends Activity>, Bundle> value) {
        try {
            Activity act = activityInfo.act.get();
            Method[] methods = act.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(BeforeClose.class)) {
                    int modifiers = method.getModifiers();
                    if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
                        Log.e(TAG, String.format("%s # %s must not be static and must be public", act.getClass().getName(), method.getName()));
                    }
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length > 1) {
                        Log.e(TAG, String.format("%s # %s must have only 1 parameter", act.getClass().getName(), method.getName()));
                    } else if (parameterTypes.length > 0) {
                        if (TextUtils.equals(parameterTypes[0].getName(), Bundle.class.getName())) {
                            method.invoke(act, value.second);
                            return;
                        }
                    }
                    method.invoke(act);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doOnProcessor(ActivityInfo activityInfo, Pair<Class<? extends Activity>, Bundle> value) {
        MethodInfo methodInfo = methodProcessor.getMethodInfo(value.first);
        methodInfo.invoke(activityInfo.act.get(), value.second);
    }


    private class ActivityInfo {
        final WeakReference<Activity> act;
        final int index;

        ActivityInfo(Activity activity, int index) {
            this.act = new WeakReference<>(activity);
            this.index = index;
        }
    }

}
