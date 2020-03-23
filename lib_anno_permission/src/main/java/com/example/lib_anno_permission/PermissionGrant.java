package com.example.lib_anno_permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface PermissionGrant {
    int value();
}
