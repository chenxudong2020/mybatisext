package com.ext.mybatisext.activerecord.config;

import org.apache.ibatis.reflection.MetaClass;

import java.lang.reflect.Method;

public class MybatisVersionAdaptorWrapper {


    private static Object defaultReflectorFactory;

    static {
        try {

            Class defaultReflectorFactoryClass=defaultReflectorFactoryClass = Class.forName("org.apache.ibatis.reflection.DefaultReflectorFactory");
            defaultReflectorFactory=defaultReflectorFactoryClass.newInstance();
        } catch (Exception e) {

            defaultReflectorFactory=null;
            e.printStackTrace();
        }
    }



    public static MetaClass forClass(Class<?> type){
        Class metaCLassz=MetaClass.class;
        try {
            Method method=metaCLassz.getDeclaredMethod("forClass",java.lang.Class.class,  Class.forName("org.apache.ibatis.reflection.ReflectorFactory"));
            return (MetaClass)method.invoke(null,type,defaultReflectorFactory);
        }catch (Exception e1){
            try {
                Method forClassMethod=metaCLassz.getDeclaredMethod("forClass",Class.class);
                return (MetaClass)forClassMethod.invoke(null,type);
            }catch (Exception e2){
                return null;
            }

        }


    }
}
