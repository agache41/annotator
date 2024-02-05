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

package io.github.agache41.annotator.predicate;

import io.github.agache41.annotator.accessor.Accessor;
import io.github.agache41.annotator.annotator.Annotator;

import java.util.function.Predicate;

/**
 * <pre>
 * Implementation for the Predicate for filtering the annotation in views.
 * Example:
 * Annotator.of(value)
 *          .getAnnotations(annotationClass)
 *          .filter(AreInDefaultOrInView.of(view))
 *          .collect(Collectors.toList());
 * </pre>
 *
 * @param <M> the type parameter
 */
public class AreInDefaultOrInView<M> implements Predicate<M> {

    public static final String DEFAULT = "default";

    private final String viewValue;

    /**
     * <pre>
     * Instantiates a new AreInDefaultOrInView based on the view name.
     * </pre>
     *
     * @param viewValue the view value
     */
    public AreInDefaultOrInView(final String viewValue) {
        this.viewValue = viewValue;
    }

    /**
     * <pre>
     * Static provider method used to create a AreInDefaultOrInView based on the view name.
     * </pre>
     *
     * @param <M>       the type parameter
     * @param viewValue the view value
     * @return the are in default or in view
     */
    public static <M> AreInDefaultOrInView<M> of(final String viewValue) {
        return new AreInDefaultOrInView(viewValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(final M m) {
        final Accessor<?> viewAcc = Annotator
                .of(m)
                .getAccessor("view");
        if (viewAcc == null) {
            return true; // if the Annotation does not have the view field, it will be automatically allowed.
        }
        final Object actualValue = viewAcc.get(m);
        return this.viewValue.equals(actualValue) || DEFAULT.equalsIgnoreCase(actualValue.toString());
    }
}
