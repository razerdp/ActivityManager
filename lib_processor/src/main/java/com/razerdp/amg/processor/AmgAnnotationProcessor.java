package com.razerdp.amg.processor;

import com.google.auto.service.AutoService;
import com.razerdp.amg.annotation.BeforeClose;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
 * package android.demo; // PackageElement
 * <p>
 * // TypeElement
 * public class DemoClass {
 * <p>
 * // VariableElement
 * private boolean mVariableType;
 * <p>
 * // VariableElement
 * private VariableClassE m VariableClassE;
 * <p>
 * // ExecuteableElement
 * public DemoClass () {
 * }
 * <p>
 * // ExecuteableElement
 * public void resolveData (Demo data   //TypeElement ) {
 * }
 * }
 */
@AutoService(Processor.class)
public class AmgAnnotationProcessor extends AbstractProcessor {

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
                    loge("@BeforeClose is only valid for methods");
                }
            }
        }

        if (!methodClassed.isEmpty()) {
            return createFile();
        }


        return false;
    }

    private boolean createFile() {
        try {
            JavaFile javaFile = JavaFile.builder(BeforeClose.class.getPackage().getName(), createType())
                    .addFileComment("Generated code from com.razerdp.amg.Amg. Do not modify!")
                    .build();
            javaFile.writeTo(mFiler);
            return true;
        } catch (IOException e) {
            loge(e.getMessage());
            loge("Can not write Amg process file");
            e.printStackTrace();
        }
        return false;
    }

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
