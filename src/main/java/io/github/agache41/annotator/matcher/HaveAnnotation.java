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

package io.github.agache41.annotator.matcher;

import io.github.agache41.annotator.annotator.Annotator;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <pre>
 * Reference value for matching a specific annotation type.
 * Example :
 * Annotator.of(new MarkedClass())
 *          .getAnnotationsThat(HaveAnnotation.ofType(Extends.class))
 *
 * </pre>
 *
 * @param <M> the type parameter
 */
public class HaveAnnotation<M extends Annotation> implements Matcher<Object, M> {

    private final Class<M> annotationClass;

    /**
     * <pre>
     * Instantiates a new AnExtendsValue based on the expected type.
     * </pre>
     *
     * @param annotationClass the annotation class
     */
    public HaveAnnotation(final Class<M> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /**
     * <pre>
     * Static provider method used to create a HaveAnnotation based on the expected type.
     * </pre>
     *
     * @param <M>        the type parameter
     * @param annotation the annotation
     * @return the have annotation
     */
    public static final <M extends Annotation> HaveAnnotation<M> ofType(final Class<M> annotation) {
        return new HaveAnnotation<>(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<M> match(final Object value) {
        return (Stream<M>) Annotator
                .of(value)
                .getAnnotations()
                .filter(a -> Objects.equals(a
                                                    .annotationType(), this.annotationClass));
    }
}
