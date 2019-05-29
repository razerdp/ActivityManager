package com.razerdp.amg.processor;

import com.razerdp.amg.annotation.BeforeClose;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by razerdp on 2019/5/27
 */
public final class AmgAnnotationProcessor extends AbstractProcessor {

    private final HashMap<String, InnerInfo> methodClassed = new HashMap<>();


    private static final String FIELD_MAP = "ACTIVITY_METHOD_MAP";
    private Elements mElementUtils;
    private Filer mFiler;
    private Types mTypeUtils;

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
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BeforeClose.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        logi("======== Amg process is running =======");
        try {
            methodClassed.clear();

            if (env.processingOver()) {
                if (!annotations.isEmpty()) {
                    loge("Unexpected processing state: annotations still available after processing over");
                    return false;
                }
            }
            if (annotations.isEmpty()) {
                return false;
            }

            Set<? extends Element> elements = env.getElementsAnnotatedWith(BeforeClose.class);
            for (Element element : elements) {
                if (element.getKind() != ElementKind.METHOD) {
                    loge("@BeforeClose is only valid for methods");
                    continue;
                }
                ExecutableElement method = (ExecutableElement) element;
                if (checkAccess(method)) {
                    String className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
                    List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                    if (parameters.size() > 1) {
                        loge(String.format("%s # %s must have only 1 parameter", className, element.getSimpleName()));
                        continue;
                    }
                    boolean hasBundle = false;
                    if (parameters.size() > 0) {
                        VariableElement param = parameters.get(0);
                        TypeMirror paramType = param.asType();
                        hasBundle = Define.ClassesName.CLASS_BUNDLE.equals(paramType.toString());
                    }

                    InnerInfo methodInfo = methodClassed.get(className);
                    if (methodInfo == null) {
                        methodInfo = new InnerInfo(method, hasBundle);
                    }

                    methodClassed.put(className, methodInfo);
                }
            }

            if (!methodClassed.isEmpty()) {
                createFile();
            }
        } catch (RuntimeException e) {
            loge("Unexpected error in AmgAnnotationProcessor: " + e.getMessage());
        }
        return false;
    }

    private void createFile() {
        try {
            logi("======== generate file =======");
            JavaFile javaFile = JavaFile.builder(Define.AMG_PACKAGE, createType())
                    .addFileComment("Generated code from com.razerdp.amg.Amg. Do not modify!")
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            loge(e.getMessage());
            loge("Can not write Amg process file");
        }
    }


    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(Define.GENERATE_FILE_NAME)
                .addModifiers(PUBLIC, FINAL);
        result.addSuperinterface(ClassName.get(mElementUtils.getTypeElement(Define.CLASS_IANNOTATION_METHOD_PROCESSOR)));
        result.addField(createMapField());
        result.addMethod(createInterfaceMethod());
        result.addStaticBlock(fillStaticBlock());
        return result.build();
    }


    private boolean checkAccess(ExecutableElement element) {
        String className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();

        if (element.getModifiers().contains(Modifier.STATIC)) {
            loge(String.format("%s # %s must not be static", className, element.getSimpleName()));
            return false;
        }

        if (!element.getModifiers().contains(PUBLIC)) {
            loge(String.format("%s # %s must be public", className, element.getSimpleName()));
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

    //region  type

    private FieldSpec createMapField() {
        ParameterizedTypeName typeMap = ParameterizedTypeName.get(ClassName.get(HashMap.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement(Define.ClassesName.CLASS_ACTIVITY)))),
                ClassName.get(mElementUtils.getTypeElement(Define.CLASS_METHOD)));
        FieldSpec.Builder result = FieldSpec.builder(typeMap, FIELD_MAP)
                .addModifiers(PRIVATE, STATIC, FINAL)
                .initializer("new $T()", typeMap);
        return result.build();
    }

    private MethodSpec createInterfaceMethod() {
        ParameterizedTypeName param = ParameterizedTypeName.get(ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement(Define.ClassesName.CLASS_ACTIVITY))));
        MethodSpec.Builder result = MethodSpec.methodBuilder(Define.MethodName.INTERFACE_IMPLEMENTS_NAME)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(param, "actClass").build())
                .addStatement("return " + FIELD_MAP + ".get($N)", "actClass")
                .returns(ClassName.get(mElementUtils.getTypeElement(Define.CLASS_METHOD)));
        return result.build();
    }

    private CodeBlock fillStaticBlock() {
        CodeBlock.Builder result = CodeBlock.builder();
        final String paramMapFormat = FIELD_MAP + ".put($T.class, new $T($S, $T.class, $L))";
        for (HashMap.Entry<String, InnerInfo> entry : methodClassed.entrySet()) {
            TypeName classType = ClassName.get(entry.getValue().element.getEnclosingElement().asType());
            result.addStatement(paramMapFormat,
                    classType,
                    ClassName.get(mElementUtils.getTypeElement(Define.CLASS_METHOD)),
                    entry.getValue().element.getSimpleName(),
                    classType,
                    entry.getValue().hasBundle);
        }
        return result.build();
    }

    //endregion

}
