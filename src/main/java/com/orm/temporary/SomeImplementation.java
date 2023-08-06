package com.orm.temporary;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SomeImplementation implements SomeInterface<String> {

    public static void main(String[] args) {
        SomeImplementation someImplementation = new SomeImplementation();
        System.out.println(someImplementation.getGenericInterfaceType());
    }


    public Class getGenericInterfaceType() {
        Class clazz = this.getClass();
        Class[] classes = clazz.getInterfaces();
        for (Class clazzz : classes) {
            if (clazzz.equals(SomeInterface.class)) {
                System.out.println(" found!");
            }
        }
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Class<?> typeArgument = (Class<?>) typeArguments[0];
        return typeArgument;
    }
}

