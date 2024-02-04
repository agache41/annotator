/*
 *    Copyright 2022-2023  Alexandru Agache
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.agache41.annotator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * Collection of helper methods.
 * </pre>
 */
public class Helper {
    /**
     * <pre>
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     *
     * public interface GenericInterfaceC{@literal <}T,R...{@literal >}
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC{@literal <}ActualTypeT,ActualType...{@literal >}
     *
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     *
     * returns ActualTypeT as Type
     *
     * </pre>
     *
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @return the Actual type
     */
    public static Type getActualTypeForGenericInterfaceByIndex(Class<?> clazz,
                                                               final Class<?> genericInterface,
                                                               final int paramIndex) {
        final String genericTypeName = genericInterface.getTypeName();
        while (!Object.class.equals(clazz)) {
            for (final Type genericType : clazz.getGenericInterfaces()) {
                if (genericType.getTypeName()
                               .startsWith(genericTypeName)) {
                    final ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    return typeArguments[paramIndex];
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * <pre>
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     *
     * public interface GenericInterfaceC{@literal <}T,R...{@literal >}
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC{@literal <}ActualTypeT,ActualType...{@literal >}
     *
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     * returns ActualTypeT.class
     * </pre>
     *
     * @param <T>              the return type
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @return the Actual type
     */
    public static <T> Class<T> getActualClassForGenericInterfaceByIndex(final Class<?> clazz,
                                                                        final Class<?> genericInterface,
                                                                        final int paramIndex) {
        final Type typeArgument = getActualTypeForGenericInterfaceByIndex(clazz, genericInterface, paramIndex);
        if (typeArgument instanceof Class) {
            return (Class<T>) typeArgument;
        }
        if (typeArgument instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) typeArgument).getRawType();
        } else {
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
        }
    }

    /**
     * <pre>
     * Given a class that implements a generic interface with Type Parameters
     * it returns the actual type of the Type Parameters in the implementation.
     * If the given class extends the generic interface through a type hierarchy
     * the function loops through the super interfaces of the class until it finds
     * the generic interface.
     * Example
     *
     * public interface GenericInterfaceC{@literal <}T,R...{@literal >}
     * public class GivenClassA extends B
     * public class B extends C
     * public class C implements D
     * public interface D implements GenericInterfaceC{@literal <}ActualTypeT,ActualType...{@literal >}
     *
     * getActualTypeForGenericInterfaceByIndex(GivenClassA.class,GenericInterfaceC.class,0)
     *
     * returns ActualTypeT.class
     * </pre>
     *
     * @param clazz            the class where the evaluation is done
     * @param genericInterface the looked-up interface
     * @param paramIndex       the index of the type
     * @return the Actual type
     */
    public static String getActualTypeNameForGenericInterfaceByIndex(final Class<?> clazz,
                                                                     final Class<?> genericInterface,
                                                                     final int paramIndex) {
        final Type typeArgument = getActualTypeForGenericInterfaceByIndex(clazz, genericInterface, paramIndex);
        if (typeArgument != null) {
            return typeArgument.getTypeName();
        } else {
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
        }
    }


    /**
     * <pre>
     * Gets actual type for generic type by index.
     * </pre>
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param paramIndex the param index
     * @return the actual type for generic type by index
     */
    public static <T> Class<T> getActualTypeForGenericTypeByIndex(final Class<?> clazz,
                                                                  final int paramIndex) {
        final Type[] typeArguments = clazz.getTypeParameters();
        final Type typeArgument = typeArguments[paramIndex];
        if (typeArgument instanceof Class) {
            return (Class<T>) typeArgument;
        }
        if (typeArgument instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) typeArgument).getRawType();
        } else {
            throw new RuntimeException(" unknown typeArgument type for class " + clazz.getSimpleName());
        }
    }


    /**
     * <pre>
     * Gets method return type.
     * </pre>
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param methodName the method name
     * @return the method return type
     */
    public static <T> Class<T> getMethodReturnType(final Class<?> clazz,
                                                   final String methodName) {
        final Method[] declaredMethods = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (methodName.equals(method.getName())) {
                return (Class<T>) method.getReturnType();
            }
        }
        throw new RuntimeException("No such method " + methodName + " was found in Class " + clazz.getSimpleName());
    }

    /**
     * <pre>
     * Unpack annotation stream.
     * </pre>
     *
     * @param a the a
     * @return the stream
     */
    public static Stream<Annotation> unpackAnnotation(final Annotation a) {
        final Stream<Annotation> singleStream = Stream.of(a);
        final Class<? extends Annotation> annotationType = a.annotationType();
        try {
            // get the method
            final Method value = annotationType.getMethod("value");
            // invoke it
            final Object result = value.invoke(a);
            if (result != null && result instanceof Annotation[]) {
                return Stream.of((Annotation[]) result);
            }
        } catch (final Exception e) {
            //
        }
        return singleStream;
    }

    /**
     * <pre>
     * Unpack annotations list.
     * </pre>
     *
     * @param input the input
     * @return the list
     */
    public static List<Annotation> unpackAnnotations(final List<Annotation> input) {
        return input.stream()
                    .flatMap(Helper::unpackAnnotation)
                    .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * <pre>
     * Unpack annotations stream.
     * </pre>
     *
     * @param input the input
     * @return the stream
     */
    public static Stream<Annotation> unpackAnnotations(final Stream<Annotation> input) {
        return input.flatMap(Helper::unpackAnnotation);
    }
}
