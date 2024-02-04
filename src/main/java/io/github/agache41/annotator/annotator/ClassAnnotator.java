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

package io.github.agache41.annotator.annotator;

import io.github.agache41.annotator.Helper;
import io.github.agache41.annotator.accessor.Accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * Implementation of the Annotator Pattern (Annotate Interface) for Types (classes).
 * </pre>
 *
 * @param <T> the type parameter
 */
public class ClassAnnotator<T> implements Annotate<Class<T>> {

    private static final Map<Class<?>, ClassAnnotator<?>> classAnnotatorMap = new ConcurrentHashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<String, Accessor<?>> accessors = new HashMap<>();
    private final Set<Method> methods = new HashSet<>();
    private final List<Annotation> annotations = new ArrayList<>();
    private final Class<T> clazz;

    private ClassAnnotator(final Class<T> clazz) {
        this.clazz = clazz;
        Class<?> classType = clazz;
        while (classType != null && (!classType.equals(Object.class))) {
            for (final Field field : classType.getDeclaredFields()) {
                this.fields.put(field.getName(), field);
            }
            Collections.addAll(this.methods, classType.getDeclaredMethods());
            this.annotations.addAll(Helper.unpackAnnotations(Stream.of(classType.getDeclaredAnnotations()))
                                          .collect(Collectors.toList()));
            classType = classType.getSuperclass();
        }
        if (!this.clazz.isAnnotation()) {
            this.fields
                    .keySet()
                    .stream()
                    .map(fieldName -> new Accessor<>(  //
                                                       this.fields.get(fieldName) //
                                                                  .getType(),
                                                       this.clazz,//
                                                       this.fields.get(fieldName)))//
                    .flatMap(Accessor::expand)
                    .forEach(accessor -> this.accessors.put(accessor.getName(), accessor));
        } else {
            this.methods
                    .stream()
                    .map(method -> new Accessor<>(method.getReturnType(), this.clazz, method))
                    .flatMap(Accessor::expand)
                    .forEach(accessor -> this.accessors.put(accessor.getName(), accessor));
        }
    }

    /**
     * <pre>
     * Creates the Annotator based on the given Class.
     * </pre>
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the class annotator
     */
    public static synchronized <T> ClassAnnotator of(final Class<T> clazz) {
        //todo: refactor all like this
        ClassAnnotator<?> classAnnotator = classAnnotatorMap.get(clazz);
        if (classAnnotator == null) {
            classAnnotator = new ClassAnnotator<>(clazz);
            classAnnotatorMap.put(clazz, classAnnotator);
        }
        return classAnnotator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> get() {
        return this.clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Annotation> getAnnotations() {
        return this.annotations.stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass,
                                                  final boolean throwOnFailure) {
        final A annotation = this.clazz.getAnnotation(annotationClass);
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found on class " + this.clazz.getSimpleName());
        }
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //todo: generalize
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(final Class<I> implementedInterface,
                                                                       final boolean throwOnFailure) {
        final Class[] classes = this.clazz.getInterfaces();
        int index = -1;
        for (int i = 0; i < classes.length; i++) {
            if (implementedInterface.equals(classes[i])) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new RuntimeException(" Interface " + implementedInterface.getSimpleName() + " was not implemented in class" + this.clazz.getSimpleName() + "!");
        }
        final ParameterizedType parameterizedType = (ParameterizedType) this.clazz.getGenericInterfaces()[index];
        return (Class<?>[]) parameterizedType.getActualTypeArguments();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Field> getFields() {
        return this.fields
                .values()
                .stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getField(final String name) {
        return this.fields.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Method> getMethods() {
        return this.methods.stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Accessor<?>> getAccessors() {
        return this.accessors
                .values()
                .stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor<?> getAccessor(final String name) {
        if (!this.accessors.containsKey(name)) {
            throw new IllegalArgumentException("No such method or field " + name + " in " + this.clazz.getSimpleName() + "!");
        }
        return this.accessors.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Class<" + this.clazz.getSimpleName() + ">";
    }
}
