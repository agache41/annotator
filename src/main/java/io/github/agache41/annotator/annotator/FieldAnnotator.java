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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * <pre>
 * Implementation of the Annotator Pattern (Annotate Interface) for Fields.
 * </pre>
 */
public class FieldAnnotator implements Annotate<Field> {

    private static final Map<Field, FieldAnnotator> fieldAnnotatorMap = new ConcurrentHashMap<>();
    private final Field field;

    private FieldAnnotator(final Field field) {
        this.field = field;
    }

    /**
     * <pre>
     * Creates the Annotator based on the given Field.
     * </pre>
     *
     * @param field the field
     * @return the field annotator
     */
    public static FieldAnnotator of(final Field field) {
        return fieldAnnotatorMap.computeIfAbsent(field, FieldAnnotator::new);
    }

    /**
     * <pre>
     * Get field.
     * </pre>
     *
     * @return the field
     */
    @Override
    public Field get() {
        return this.field;
    }

    /**
     * <pre>
     * Gets annotations.
     * </pre>
     *
     * @return the annotations
     */
    @Override
    public Stream<Annotation> getAnnotations() {
        return Helper.unpackAnnotations(Stream.of(this.field.getAnnotations()));
    }

    /**
     * <pre>
     * Gets annotation.
     * </pre>
     *
     * @param <A>             the type parameter
     * @param annotationClass the annotation class
     * @param throwOnFailure  the throw on failure
     * @return the annotation
     */
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass,
                                                  final boolean throwOnFailure) {
        final A annotation = this.field.getAnnotation(annotationClass);
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found on field " + this.field.getName());
        }
        return annotation;
    }

    /**
     * <pre>
     * Get parameterized types for implemented interface class [ ].
     * </pre>
     *
     * @param <I>                  the type parameter
     * @param implementedInterface the implemented interface
     * @param throwOnFailure       the throw on failure
     * @return the class [ ]
     */
    @Override
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(final Class<I> implementedInterface,
                                                                       final boolean throwOnFailure) {
        throw new IllegalStateException(" Fields do not have implemented interfaces!");
    }

    /**
     * <pre>
     * Gets fields.
     * </pre>
     *
     * @return the fields
     */
    @Override
    public Stream<Field> getFields() {

        throw new IllegalStateException(" Fields do not have fields!");
    }

    /**
     * <pre>
     * Gets field.
     * </pre>
     *
     * @param name the name
     * @return the field
     */
    @Override
    public Field getField(final String name) {
        throw new IllegalStateException(" Fields do not have fields!");
    }

    /**
     * <pre>
     * Gets methods.
     * </pre>
     *
     * @return the methods
     */
    @Override
    public Stream<Method> getMethods() {
        throw new IllegalStateException(" Fields do not have methods!");
    }

    /**
     * <pre>
     * Gets accessors.
     * </pre>
     *
     * @return the accessors
     */
    @Override
    public Stream<Accessor<?>> getAccessors() {
        throw new IllegalStateException(" Fields do not have accessors!");
    }

    /**
     * <pre>
     * Gets accessor.
     * </pre>
     *
     * @param name the name
     * @return the accessor
     */
    @Override
    public Accessor<Field> getAccessor(final String name) {
        throw new IllegalStateException(" Fields do not have accessors!");
    }

    /**
     * <pre>
     * To string string.
     * </pre>
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.field.getDeclaringClass()
                         .getSimpleName() + "." + this.field.getName();
    }
}
