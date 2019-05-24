package com.razerdp.amg.annotation;

/**
 * Created by 大灯泡 on 2019/5/24
 * <p>
 * Description：
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BeforeClose {

    int priority();

}
