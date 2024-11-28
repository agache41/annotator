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
import io.github.agache41.annotator.matcher.Matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * <pre>
 * Base interface for the Annotator Pattern.
 * The interface defines a fixed set of methods that, using the Annotator class,
 * can be called on scopes like type, field, method ..etc.
 * The scope of the Annotator Pattern can be a class (type), a field,a method or an annotation.
 * Example :
 * Annotator.of(new MarkedClass())
 *          .getAnnotations()
 * or
 * Annotator.of(new MarkedClass())
 *          .getFieldsThat(HaveAnnotation.ofType(TestExtends.class))
 * MarkedClass serves here as the scope of the Annotate pattern.
 * In this way very complex annotated structure of java beans can be queried very easily,
 * replacing the implementing code for annotation lookup with a compact api.
 * </pre>
 *
 * @param <T> the type parameter
 */
public interface Annotate<T> {

    /**
     * Enables DEBUG mode (internal use only)
     */
    boolean DEBUG = false;

    /**
     * <pre>
     * Gets the underlining "annotated" scope (class, field, method ..etc)
     * </pre>
     *
     * @return the scope
     */
    T get();

    /**
     * <pre>
     * Gets the annotations present in scope.
     * </pre>
     *
     * @return the annotations stream
     */
    Stream<Annotation> getAnnotations();

    /**
     * <pre>
     * Gets the annotation present in scope of the given type parameter.
     * </pre>
     *
     * @param <A>             the type parameter
     * @param annotationClass the annotation class
     * @param throwOnFailure  if true, will throw when no annotation is found.
     * @return the annotation
     */
    <A extends Annotation> A getAnnotation(Class<A> annotationClass,
                                           boolean throwOnFailure);

    /**
     * <pre>
     * Gets the parameterized types used when implementing the given interface class
     * </pre>
     *
     * @param <I>                  the type parameter
     * @param implementedInterface the implemented interface
     * @param throwOnFailure       if true, will throw when no parameterized types are found.
     * @return the class [ ]
     */
    <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface,
                                                                boolean throwOnFailure);

    /**
     * <pre>
     * Returns the fields present in scope.
     * </pre>
     *
     * @return the fields
     */
    Stream<Field> getFields();

    /**
     * <pre>
     * Returns the field with the given name present in scope.
     * </pre>
     *
     * @param name the name
     * @return the field
     */
    Field getField(String name);

    /**
     * <pre>
     * Returns the methods present in scope.
     * </pre>
     *
     * @return the methods
     */
    Stream<Method> getMethods();

    /**
     * <pre>
     * Returns the accessors present in scope.
     * </pre>
     *
     * @return the accessors
     */
    Stream<Accessor<?>> getAccessors();

    /**
     * <pre>
     * Returns the accessor with the given name present in scope.
     * </pre>
     *
     * @param name the name
     * @return the accessor
     */
    Accessor<?> getAccessor(String name);

    /**
     * <pre>
     * Implementation of the Object.toString() method
     * </pre>
     * @see Object
     * @return the representation string
     */
    @Override
    String toString();

    /**
     * <pre>
     * Tells if in the current scope contains annotation of the given type.
     * </pre>
     *
     * @param <A>   the generic type parameter.
     * @param clazz the annotation type.
     * @return true if the scope contains annotation of the given type, false otherwise.
     */
    default <A extends Annotation> boolean hasAnnotation(final Class<A> clazz) {
        return this
                .getAnnotations(clazz)
                .findAny()
                .isPresent();
    }

    /**
     * <pre>
     * Gets the annotations from the current scope of the given type.
     * </pre>
     *
     * @param <A>   the generic type parameter
     * @param clazz the annotation type.
     * @return the stream of annotations
     */
    default <A extends Annotation> Stream<A> getAnnotations(final Class<A> clazz) {
        return (Stream<A>) this
                .getAnnotations()
                .filter(annotation -> clazz
                        .equals(annotation
                                        .annotationType()));
    }

    /**
     * <pre>
     * Gets the annotations from the current scope that match the given matcher.
     * </pre>
     *
     * @param matcher the matcher
     * @return the stream of annotations
     */
    default Stream<Annotation> getAnnotationsThat(final Matcher<Object, ?> matcher) {
        return this
                .getAnnotations()
                .filter(matcher::matches);
    }

    /**
     * <pre>
     * Gets the fields from the current scope that match the given matcher.
     * </pre>
     *
     * @param matcher the matcher
     * @return the stream of fields
     */
    default Stream<Field> getFieldsThat(final Matcher<Object, ?> matcher) {
        return this
                .getFields()
                .filter(matcher::matches);
    }

    /**
     * <pre>
     * Gets the methods from the current scope that match the given matcher.
     * </pre>
     *
     * @param matcher the matcher
     * @return the stream of methods
     */
    default Stream<Method> getMethodsThat(final Matcher<Object, ?> matcher) {
        return this
                .getMethods()
                .filter(matcher::matches);
    }

    /**
     * <pre>
     * Gets the accessors from the current scope that match the given matcher.
     * </pre>
     *
     * @param matcher the matcher
     * @return the stream of accessors
     */
    default Stream<Accessor<?>> getAccessorsThat(final Matcher<Object, ?> matcher) {
        return this
                .getAccessors()
                .filter(matcher::matches);
    }
}

