package com.example.lib_utils;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

public class Utils {

    public static boolean checkTargetIsMethod(Element element, Messager logger){
        if (element.getKind() != ElementKind.METHOD){
            if (logger != null){
                logger.printMessage(Diagnostic.Kind.ERROR, "只作用在方法上");
            }
            return false;
        }

        Set<Modifier> classModifier = element.getModifiers();
        if (classModifier.contains(Modifier.PRIVATE)){
            logger.printMessage(Diagnostic.Kind.ERROR, "非public方法");
            return false;
        }
        if (classModifier.contains(Modifier.ABSTRACT)){
            logger.printMessage(Diagnostic.Kind.ERROR, "abstract方法");
            return true;
        }
        return true;
    }

}
