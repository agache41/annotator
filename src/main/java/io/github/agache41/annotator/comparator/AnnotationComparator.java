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

package io.github.agache41.annotator.comparator;

import java.lang.annotation.Annotation;
import java.util.Comparator;

/**
 * <pre>
 * Comparator for annotation, comparing the value in a given field.
 * </pre>
 */
public class AnnotationComparator implements Comparator<Annotation> {
    private final String method;

    /**
     * <pre>
     * Instantiates a new Annotation comparator, based on the name of the field ( annotation method )
     * </pre>
     *
     * @param method the method name
     */
    public AnnotationComparator(final String method) {
        this.method = method;
    }

    /**
     * <pre>
     * Static provider method used to create a comparator based on the method name.
     * </pre>
     *
     * @param method the method
     * @return the annotation comparator
     */
    public static AnnotationComparator invokingMethod(final String method) {
        return new AnnotationComparator(method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Annotation o1,
                       final Annotation o2) {
        throw new RuntimeException("not implemented!");
    }
}
