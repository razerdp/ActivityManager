package com.razerdp.amg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 大灯泡 on 2019/5/29
 * <p>
 * Description：
 */
public class FinishBuilder {

    Map<Class<? extends Activity>, Pair<Class<? extends Activity>, Bundle>> mClosedMap = new HashMap<>();

    FinishBuilder() {

    }

    public FinishBuilder append(Class<? extends Activity> target, Bundle bundle) {
        if (target == null) return this;
        mClosedMap.put(target, Pair.create(target, bundle));
        return this;
    }

    public void finish() {
        Amg.getInstance().process(this);
    }


}
