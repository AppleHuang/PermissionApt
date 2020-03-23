package com.example.lib_compiler_permission;

import com.example.lib_anno_permission.PermissionGrant;
import com.example.lib_anno_permission.PermissionRational;
import com.example.lib_anno_permission.PermissionReject;
import com.example.lib_utils.Utils;
import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "com.example.lib_anno_permission.PermissionGrant",
        "com.example.lib_anno_permission.PermissionReject",
        "com.example.lib_anno_permission.PermissionRational"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PermissionAbstractProccesor extends AbstractProcessor {

    private Elements elementUtils;
    private Messager logger;

    private Map<String, ClassInfo> map;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        map = new HashMap<>();
        elementUtils = processingEnv.getElementUtils();
        logger = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        map.clear();
        if (!handleAnnoInfo(roundEnv, PermissionGrant.class)){
            return false;
        }
        if (!handleAnnoInfo(roundEnv, PermissionReject.class)){
            return false;
        }
        if (!handleAnnoInfo(roundEnv, PermissionRational.class)){
            return false;
        }
        try {
            for (Map.Entry<String, ClassInfo> entry : map.entrySet()){
                entry.getValue().create(filer, logger);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.printMessage(Diagnostic.Kind.ERROR, "创建文件失败"+e.getMessage());
        }

        return true;
    }

    private boolean handleAnnoInfo(RoundEnvironment roundEnv, Class<? extends Annotation> annoClass) {
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(annoClass);
        for (Element element : elementSet) {
            if (!Utils.checkTargetIsMethod(element, logger)){
                continue;
            }

            ExecutableElement methodElement = (ExecutableElement) element;

            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();

            logger.printMessage(Diagnostic.Kind.NOTE, fullClassName);

            ClassInfo classInfo = map.get(fullClassName);
            if (classInfo == null){
                classInfo = new ClassInfo(classElement);
                map.put(fullClassName, classInfo);
            }

            List<? extends VariableElement> parameterList = methodElement.getParameters();
            logger.printMessage(Diagnostic.Kind.NOTE, "fullClassName: "+ fullClassName);
            logger.printMessage(Diagnostic.Kind.NOTE, "activityName: "+ classInfo.packageName);

            String methodName = methodElement.getSimpleName().toString();
            Annotation annotation = methodElement.getAnnotation(annoClass);

            if (annotation instanceof PermissionGrant){
                int requestCode = ((PermissionGrant) annotation).value();
                MethodInfo methodInfo;
                if (parameterList.size() == 0){
                    methodInfo = new MethodInfo(methodName);
                } else if (parameterList.size() == 1){
                    VariableElement variableElement = parameterList.get(0);
                    if (!"java.lang.String[]".equals(variableElement.asType().toString())){
                        logger.printMessage(Diagnostic.Kind.ERROR, "grant method must no parameter or has one string[] parameter");
                        return false;
                    }
                    methodInfo = new MethodInfo(methodName, true);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "grant method must no parameter or has one string[] parameter");
                    return false;
                }

                Map<Integer, MethodInfo> grantMap = classInfo.grantMap;
                if (!grantMap.containsKey(requestCode)){
                    grantMap.put(requestCode, methodInfo);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "requestCode "+requestCode+" is already exist in "+grantMap.get(requestCode).name +" method, conflict method is "+methodName);
                    return false;
                }
            } else if (annotation instanceof PermissionReject){
                int requestCode = ((PermissionReject) annotation).value();
                MethodInfo methodInfo;
                if (parameterList.size() == 0){
                    methodInfo = new MethodInfo(methodName);
                } else if (parameterList.size() == 1){
                    VariableElement variableElement = parameterList.get(0);
                    if (!"java.lang.String[]".equals(variableElement.asType().toString())){
                        logger.printMessage(Diagnostic.Kind.ERROR, "reject method must no parameter or has one string[] parameter");
                        return false;
                    }
                    methodInfo = new MethodInfo(methodName, true);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "reject method must no parameter or has one string[] parameter");
                    return false;
                }

                Map<Integer, MethodInfo> rejectMap = classInfo.rejectMap;
                if (!rejectMap.containsKey(requestCode)){
                    rejectMap.put(requestCode, methodInfo);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "requestCode "+requestCode+" is already exist in "+rejectMap.get(requestCode).name +" method, conflict method is "+methodName);
                    return false;
                }
            } else if (annotation instanceof PermissionRational){
                int requestCode = ((PermissionRational) annotation).value();
                MethodInfo methodInfo;
                if (parameterList.size() == 0){
                    methodInfo = new MethodInfo(methodName);
                } else if (parameterList.size() == 1){
                    VariableElement variableElement = parameterList.get(0);
                    if (!"java.lang.String[]".equals(variableElement.asType().toString())){
                        logger.printMessage(Diagnostic.Kind.ERROR, "rational method must no parameter or has one string[] parameter or two (string[], PermissionRationalCallBack)");
                        return false;
                    }
                    methodInfo = new MethodInfo(methodName, true);
                } else if (parameterList.size() == 2){
                    VariableElement variableElement = parameterList.get(0);
                    if (!"java.lang.String[]".equals(variableElement.asType().toString())){
                        logger.printMessage(Diagnostic.Kind.ERROR, "rational method must no parameter or has one string[] parameter or two (string[], PermissionRationalCallBack)");
                        return false;
                    }
                    variableElement = parameterList.get(1);
                    if (!"com.example.lib_permission.PermissionRationalCallBack".equals(variableElement.asType().toString())){
                        logger.printMessage(Diagnostic.Kind.ERROR, "rational method must no parameter or has one string[] parameter or two (string[], PermissionRationalCallBack)");
                        return false;
                    }
                    methodInfo = new MethodInfo(methodName, true, true);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "rational method must no parameter or has one string[] parameter or two (string[], PermissionRationalCallBack)");
                    return false;
                }

                Map<Integer, MethodInfo> rationalMap = classInfo.rationalMap;
                if (!rationalMap.containsKey(requestCode)){
                    rationalMap.put(requestCode, methodInfo);
                } else {
                    logger.printMessage(Diagnostic.Kind.ERROR, "requestCode "+requestCode+" is already exist in "+rationalMap.get(requestCode).name +" method, conflict method is "+methodName);
                    return false;
                }
            }
        }

        return true;
    }

}
