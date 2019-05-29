package com.razerdp.amg.utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by razerdp on 2019/5/29
 * <p>
 * Description
 */
public class Utils {
    private static Elements mElementUtils;
    private static Types mTypeUtils;

    private Utils() {

    }

    public static void init(ProcessingEnvironment env) {
        mElementUtils = env.getElementUtils();
        mTypeUtils = env.getTypeUtils();
    }

    public static String getPackageName(Element element) {
        PackageElement packageElement = mElementUtils.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

}
