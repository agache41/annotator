package com.orm.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helper {
    /**
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     * public interface GenericInterfaceC<T,R... >
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC<ActualTypeT,ActualType.... >
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     * returns ActualTypeT as Type
     *
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @return the Actual type
     */
    public static Type getActualTypeForGenericInterfaceByIndex(Class<?> clazz,
                                                               Class<?> genericInterface,
                                                               int paramIndex) {
        final String genericTypeName = genericInterface.getTypeName();
        while (!Object.class.equals(clazz)) {
            for (Type genericType : clazz.getGenericInterfaces()) {
                if (genericType.getTypeName()
                               .startsWith(genericTypeName)) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    return typeArguments[paramIndex];
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     * public interface GenericInterfaceC<T,R... >
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC<ActualTypeT,ActualType.... >
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     * returns ActualTypeT.class
     *
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @param <T>              the return type
     * @return the Actual type
     */
    public static <T> Class<T> getActualClassForGenericInterfaceByIndex(Class<?> clazz,
                                                                        Class<?> genericInterface,
                                                                        int paramIndex) {
        final Type typeArgument = getActualTypeForGenericInterfaceByIndex(clazz, genericInterface, paramIndex);
        if (typeArgument instanceof Class)
            return (Class<T>) typeArgument;
        if (typeArgument instanceof ParameterizedType)
            return (Class<T>) ((ParameterizedType) typeArgument).getRawType();
        else
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
    }

    /**
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     * public interface GenericInterfaceC<T,R... >
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC<ActualTypeT,ActualType.... >
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     * returns ActualTypeT.class
     *
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @return the Actual type
     */
    public static String getActualTypeNameForGenericInterfaceByIndex(Class<?> clazz,
                                                                     Class<?> genericInterface,
                                                                     int paramIndex) {
        final Type typeArgument = getActualTypeForGenericInterfaceByIndex(clazz, genericInterface, paramIndex);
        if (typeArgument != null)
            return typeArgument.getTypeName();
        else
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
    }


    public static <T> Class<T> getActualTypeForGenericTypeByIndex(Class<?> clazz,
                                                                  int paramIndex) {
        Type[] typeArguments = clazz.getTypeParameters();
        Type typeArgument = typeArguments[paramIndex];
        if (typeArgument instanceof Class)
            return (Class<T>) typeArgument;
        if (typeArgument instanceof ParameterizedType)
            return (Class<T>) ((ParameterizedType) typeArgument).getRawType();
        else
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
    }


    public static <T> Class<T> getMethodReturnType(Class<?> clazz,
                                                   String methodName) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (methodName.equals(method.getName())) {
                return (Class<T>) method.getReturnType();
            }
        }
        throw new RuntimeException("No such method " + methodName + " was found in Class " + clazz.getSimpleName());
    }

    public static Stream<Annotation> unpackAnnotation(Annotation a) {
        Stream<Annotation> singleStream = Stream.of(a);
        Class<? extends Annotation> annotationType = a.annotationType();
        try {
            // get the method
            Method value = annotationType.getMethod("value");
            // invoke it
            Object result = value.invoke(a);
            if (result != null && result instanceof Annotation[]) {
                return Stream.of((Annotation[]) result);
            }
        } catch (Exception e) {
            //
        }
        return singleStream;
    }

    public static List<Annotation> unpackAnnotations(List<Annotation> input){
        return input.stream()
                     .flatMap(Helper::unpackAnnotation)
                     .collect(Collectors.toCollection(LinkedList::new));
    }

    public static Stream<Annotation> unpackAnnotations(Stream<Annotation> input){
        return input.flatMap(Helper::unpackAnnotation);
    }
}
