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

import io.github.agache41.annotator.accessor.Accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Implementation of the Annotator Pattern (Annotate Interface) for Accessors.
 */
public class AccessorAnnotator implements Annotate<Accessor<?>> {

    private static final Map<Accessor<?>, AccessorAnnotator> accessorAnnotatorMap = new ConcurrentHashMap<>();
    private final Accessor<?> accessor;

    private AccessorAnnotator(final Accessor<?> accessor) {
        this.accessor = accessor;
    }

    /**
     * <pre>
     * Creates the Annotator based on the given Accessor.
     * </pre>
     *
     * @param field the field
     * @return the accessor annotator
     */
    public static AccessorAnnotator of(final Accessor<?> field) {
        return accessorAnnotatorMap.computeIfAbsent(field, AccessorAnnotator::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor<?> get() {
        return this.accessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Annotation> getAnnotations() {
        return this.accessor
                .getAnnotations()
                .stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass,
                                                  final boolean throwOnFailure) {
        return this.accessor.getAnnotation(annotationClass, throwOnFailure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(final Class<I> implementedInterface,
                                                                       final boolean throwOnFailure) {
        throw new RuntimeException(" Fields or Methods do not have implemented interfaces!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Field> getFields() {

        throw new IllegalStateException(" Accessors do not have fields!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getField(final String name) {
        throw new IllegalStateException(" Accessors do not have fields!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Method> getMethods() {
        throw new IllegalStateException(" Accessors do not have methods!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Accessor<?>> getAccessors() {

        throw new IllegalStateException(" Accessors do not have accessors!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor<Field> getAccessor(final String name) {
        throw new IllegalStateException(" Accessors do not have accessors!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.accessor.getDeclaringClass()
                            .getSimpleName() + ":acc:" + this.accessor.getName();
    }
}
