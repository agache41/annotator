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
import io.github.agache41.annotator.predicate.AreInDefaultOrInView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Main entry class for the Annotator Pattern.
 * The class provides the method Annotator.of(..) which can be used on multiple scopes (types, fields, methods, annotation, accessors)
 * to obtain the underlining Annotate implementation.
 * </pre>
 */
public class Annotator {
    /**
     * <pre>
     * Main provider method for annotating the given value.
     * Example :
     *  Annotator.of(MarkedClass.class)
     *  Annotator.of(new MarkedClass())
     *  Field name = ...
     *  Annotator.of(name)
     * </pre>
     *
     * @param <T>   the generic type of the value
     * @param value the value to annotate.
     * @return the annotate
     */
    public static final <T> Annotate<T> of(final T value) {
        if (value instanceof Accessor) {
            return (Annotate<T>) AccessorAnnotator.of((Accessor<T>) value);
        }
        if (value instanceof Field) {
            return (Annotate<T>) FieldAnnotator.of((Field) value);
        }
        if (value instanceof Method) {
            return (Annotate<T>) MethodAnnotator.of((Method) value);
        }
        if (value instanceof Annotation) {
            final Annotation annotation = (Annotation) value;
            return ClassAnnotator.of(annotation.annotationType());
        }
        if (value instanceof Class) {
            final Class<?> clazz = (Class<?>) value;
            return ClassAnnotator.of(clazz);
        }
        return ClassAnnotator.of(value.getClass());
    }
    //todo : addMethods for every Type

    /**
     * <pre>
     * Gets the annotation available for the given view.
     * </pre>
     *
     * @param <M>             the type parameter
     * @param <T>             the type parameter
     * @param value           the value
     * @param annotationClass the annotation class
     * @param view            the view
     * @return the annotation for view
     */
    public static <M extends Annotation, T> M getAnnotationForView(final T value,
                                                                   final Class<M> annotationClass,
                                                                   final String view) {
        return getAnnotationForView(value, annotationClass, view, false);
    }

    /**
     * <pre>
     * Gets the annotation available for the given view.
     * </pre>
     *
     * @param <M>             the type parameter
     * @param <T>             the type parameter
     * @param value           the value
     * @param annotationClass the annotation class
     * @param view            the view
     * @param throwOnFailure  the throw on failure
     * @return the annotation for view
     */
    public static <M extends Annotation, T> M getAnnotationForView(final T value,
                                                                   final Class<M> annotationClass,
                                                                   final String view,
                                                                   final boolean throwOnFailure) {
        final List<M> collect = Annotator
                .of(value)
                .getAnnotations(annotationClass)
                .filter(AreInDefaultOrInView.of(view))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            if (throwOnFailure) {
                throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found in default or " + view + " view on " + value.toString());
            }
            return null;
        }
        if (collect.size() > 1) {
            throw new RuntimeException("More than one annotation of type " + annotationClass.getSimpleName() + " where found in default or " + view + " view on " + value.toString());
        }
        return collect.get(0);
    }
}
