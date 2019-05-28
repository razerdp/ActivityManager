package com.razerdp.amg.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by razerdp on 2019/5/27
 * <p>
 * Description
 */
@SupportedAnnotationTypes({"com.razerdp.amg.annotation.OnClose"})
public final class AmgAnnotationProcessor extends AbstractProcessor {

    private final HashMap<TypeElement, InnerInfo> methodClassed = new HashMap<>();


    private static final String GENERATE_FILE_NAME = "Amg_Inject";
    private static final String FIELD_MAP = "ACTIVITY_METHOD_MAP";
    private Elements mElementUtils;
    private Filer mFiler;
    private Types mTypeUtils;
    private TypeSpecHelper mSpecHelper = new TypeSpecHelper();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        System.out.println("======== Amg process is running =======");
        loge("======== Amg process is running =======");
        TypeSpec finderClass = TypeSpec.classBuilder("GGClass")
                .addModifiers(Modifier.PUBLIC)
                .build();

        JavaFile javaFile = JavaFile.builder("com.razerdp.amg", finderClass).build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            loge(e.getMessage());
            e.printStackTrace();
        }
/*
        logi("======== Amg process is running =======");
        if (env.processingOver()) {
            if (!annotations.isEmpty()) {
                loge("======= Amg process run finish with something wrong ======= ");
                return false;
            }
        }
        if (annotations.isEmpty()) {
            return false;
        }

        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (element instanceof ExecutableElement) {
                    ExecutableElement method = (ExecutableElement) element;
                    if (checkAccess(method)) {
                        List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                        if (parameters.size() > 1) {
                            loge("method must have only 1 parameter");
                            continue;
                        }
                        boolean hasBundle = false;
                        if (parameters.size() > 0) {
                            VariableElement param = parameters.get(0);
                            TypeMirror paramType = param.asType();
                            hasBundle = Define.ClassesName.CLASS_BUNDLE.equals(paramType.toString());
                        }

                        TypeElement classElement = (TypeElement) method.getEnclosingElement();
                        InnerInfo methodInfo = methodClassed.get(classElement);
                        if (methodInfo == null) {
                            methodInfo = new InnerInfo(method, hasBundle);
                        }

                        methodClassed.put(classElement, methodInfo);
                    }
                } else {
                    loge("@OnClose is only valid for methods");
                }
            }
        }

        if (!methodClassed.isEmpty()) {
            createFile();
        }
*/

        return false;
    }

/*    private void createFile() {
        try {
            JavaFile javaFile = JavaFile.builder(OnClose.class.getPackage().getName(), createType())
                    .addFileComment("Generated code from com.razerdp.amg.Amg. Do not modify!")
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            loge(e.getMessage());
            loge("Can not write Amg process file");
        }
    }*/

    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(GENERATE_FILE_NAME)
                .addModifiers(PUBLIC, FINAL);
        //implements
        result.addSuperinterface(ClassName.get(mElementUtils.getTypeElement(Define.CLASS_IANNOTATION_METHOD_PROCESSOR)));

        //field
        result.addField(mSpecHelper.createMapField());

        //implements method
        result.addMethod(mSpecHelper.createInterfaceMethod());

        return result.build();
    }


    private boolean checkAccess(ExecutableElement element) {
        if (element.getModifiers().contains(Modifier.STATIC)) {
            loge("method must not be static");
            return false;
        }

        if (!element.getModifiers().contains(PUBLIC)) {
            loge("method must be public");
            return false;
        }
        return true;
    }

    private void logi(String what) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, what);
    }

    private void loge(String what) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, what);
    }

    private void logw(String what) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, what);
    }


    private static class InnerInfo {
        final ExecutableElement element;
        final boolean hasBundle;

        InnerInfo(ExecutableElement element, boolean hasBundle) {
            this.element = element;
            this.hasBundle = hasBundle;
        }
    }

    private class TypeSpecHelper {
        MethodSpec createInterfaceMethod() {
            return null;
        }

        ParameterizedTypeName typeRouterMap = ParameterizedTypeName.get(ClassName.get(HashMap.class),
                //class<? extends Activity>
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement(Define.ClassesName.CLASS_ACTIVITY))),
                        //MethodInfo
                        ClassName.get(mElementUtils.getTypeElement(Define.CLASS_METHOD))));

        FieldSpec createMapField() {
            FieldSpec.Builder result = FieldSpec.builder(typeRouterMap, FIELD_MAP)
                    .addModifiers(PRIVATE, STATIC, FINAL)
                    .initializer("new %T");
            return result.build();
        }
    }
}
