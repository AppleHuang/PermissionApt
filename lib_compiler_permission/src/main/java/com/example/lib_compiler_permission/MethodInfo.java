package com.example.lib_compiler_permission;

public class MethodInfo {
    public String name;
    public boolean hasPermissionParam;
    public boolean hasRational;

    public MethodInfo(String name) {
        this.name = name;
    }

    public MethodInfo(String name, boolean hasPermissionParam) {
        this.name = name;
        this.hasPermissionParam = hasPermissionParam;
    }

    public MethodInfo(String name, boolean hasPermissionParam, boolean hasRational) {
        this.name = name;
        this.hasPermissionParam = hasPermissionParam;
        this.hasRational = hasRational;
    }
}
