package com.example.lib_compiler_permission;

import com.example.lib_permission.PermissionProxy;
import com.example.lib_permission.PermissionRationalCallBack;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class ClassInfo {
    public static final String sSuffix = "$Proxy";

    public static final String grantMethodName = "grant";
    public static final String rejectMethodName = "reject";
    public static final String rationalMethodName = "rational";

    public static final String requestCodeName = "requestCode";
    public static final String sourceName = "source";
    public static final String permissionsName = "permissions";
    public static final String rationaleCallBackName = "rationaleCallBackName";

   public final TypeElement typeElement;
   public final String className;
   public final String packageName;

   public ClassInfo(TypeElement typeElement) {
        this.typeElement = typeElement;
        String fullClassName = typeElement.getQualifiedName().toString();
        this.packageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));
        this.className = fullClassName.substring(fullClassName.lastIndexOf(".")+1);
   }

   public Map<Integer, MethodInfo> grantMap = new HashMap<>();
   public Map<Integer, MethodInfo> rejectMap = new HashMap<>();
   public Map<Integer, MethodInfo> rationalMap = new HashMap<>();


   public void create(Filer filer, Messager logger) throws Exception {
        TypeSpec.Builder typeSpecBuild = TypeSpec.classBuilder(className+sSuffix)
                .superclass(PermissionProxy.class)
             .addModifiers(Modifier.PUBLIC);

        ClassName sourceType = ClassName.get(typeElement);
        typeSpecBuild.addField(sourceType, sourceName, Modifier.PRIVATE);

        MethodSpec.Builder constructMethod = MethodSpec.constructorBuilder()
               .addModifiers(Modifier.PUBLIC)
               .addParameter(sourceType, sourceName)
               .addStatement("\tthis."+sourceName+" = "+sourceName);
        typeSpecBuild.addMethod(constructMethod.build());

        if (!grantMap.isEmpty()) {
            MethodSpec.Builder grantMethodBuild = MethodSpec.methodBuilder(grantMethodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(int.class, requestCodeName)
                    .addParameter(String[].class, permissionsName)
                    .beginControlFlow("switch (requestCode)");

            for (Map.Entry<Integer, MethodInfo> entry : grantMap.entrySet()) {
                grantMethodBuild.addCode("case $L:\n", entry.getKey());
                MethodInfo methodInfo = entry.getValue();

                if (!methodInfo.hasPermissionParam) {
                    grantMethodBuild.addStatement("\t" + sourceName + "." + methodInfo.name + "()");
                } else {
                    grantMethodBuild.addStatement("\t" + sourceName + "." + methodInfo.name + "(" + permissionsName + ")");
                }

                grantMethodBuild.addStatement("\tbreak");
            }
            typeSpecBuild.addMethod(grantMethodBuild.endControlFlow().build());
        }
         if (!rejectMap.isEmpty()) {
             MethodSpec.Builder rejectMethodBuild = MethodSpec.methodBuilder(rejectMethodName)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(void.class)
                     .addParameter(int.class, requestCodeName)
                     .addParameter(String[].class, permissionsName)
                     .beginControlFlow("switch (requestCode)");

             for (Map.Entry<Integer, MethodInfo> entry : rejectMap.entrySet()) {
                 rejectMethodBuild.addCode("case $L:\n", entry.getKey());
                 MethodInfo methodInfo = entry.getValue();

                 if (!methodInfo.hasPermissionParam) {
                     rejectMethodBuild.addStatement("\t" + sourceName + "." + methodInfo.name + "()");
                 } else {
                     rejectMethodBuild.addStatement("\t" + sourceName + "." + methodInfo.name + "(" + permissionsName + ")");
                 }

                 rejectMethodBuild.addStatement("\tbreak");
             }
             typeSpecBuild.addMethod(rejectMethodBuild.endControlFlow().build());

         }

       if (!rationalMap.isEmpty()) {
           MethodSpec.Builder rationalMethodBuild = MethodSpec.methodBuilder(rationalMethodName)
                   .addModifiers(Modifier.PUBLIC)
                   .returns(boolean.class)
                   .addParameter(int.class, requestCodeName)
                   .addParameter(String[].class, permissionsName)
                   .addParameter(PermissionRationalCallBack.class, rationaleCallBackName)
                   .beginControlFlow("switch (requestCode)");

           for (Map.Entry<Integer, MethodInfo> entry : rationalMap.entrySet()) {
               rationalMethodBuild.addCode("case $L:\n", entry.getKey());
               MethodInfo methodInfo = entry.getValue();

               if (methodInfo.hasPermissionParam && methodInfo.hasRational){
                   rationalMethodBuild.addStatement("\treturn " + sourceName + "." + methodInfo.name + "(" + permissionsName + ", "+rationaleCallBackName+")");
               } else {
                   if (!methodInfo.hasPermissionParam) {
                       rationalMethodBuild.addStatement("\treturn " + sourceName + "." + methodInfo.name + "()");
                   } else {
                       rationalMethodBuild.addStatement("\treturn " + sourceName + "." + methodInfo.name + "()");
                   }
               }

//               rationalMethodBuild.addStatement("\tbreak");
           }
           rationalMethodBuild.endControlFlow();
           rationalMethodBuild.addStatement("return true");
           typeSpecBuild.addMethod(rationalMethodBuild.build());
       }
     JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuild.build()).build();
     javaFile.writeTo(filer);
   }

}
