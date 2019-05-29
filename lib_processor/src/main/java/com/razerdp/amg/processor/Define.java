package com.razerdp.amg.processor;

/**
 * Created by razerdp on 2019/5/27
 */
final class Define {
    static final String GENERATE_FILE_NAME = "Amg$Inject";
    static final String MODEL_PACKAGE = "com.razerdp.amg.model";
    static final String AMG_PACKAGE = "com.razerdp.amg";

    static final String CLASS_IANNOTATION_METHOD_PROCESSOR = MODEL_PACKAGE + ".IAnnotationMethodProcessor";
    static final String CLASS_METHOD = MODEL_PACKAGE + ".MethodInfo";
    static final String CLASS_AMG = AMG_PACKAGE + ".Amg";

    static class ClassesName {
        static final String CLASS_BUNDLE = "android.os.Bundle";
        static final String CLASS_ACTIVITY = "android.app.Activity";
    }

    static class MethodName {
        static final String INTERFACE_IMPLEMENTS_NAME = "getMethodInfo";
    }

}
