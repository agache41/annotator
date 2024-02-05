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

import io.github.agache41.annotator.accessor.Accessor;
import io.github.agache41.annotator.annotator.Annotator;

import java.util.function.ToIntFunction;

/**
 * <pre>
 * Extractor class of int values from annotation based on the annotation method name.
 * </pre>
 *
 * @param <A> the type parameter
 */
public class IntValue<A> implements ToIntFunction<A> {
    private final String method;
    /**
     * <pre>
     * The Value.
     * </pre>
     */
    int value;

    /**
     * <pre>
     * Instantiates a new IntValue, based on the method name.
     * </pre>
     *
     * @param method the method
     */
    public IntValue(final String method) {
        this.method = method;
    }

    /**
     * <pre>
     * Static provider method used to create a IntValue based on the method name.
     * </pre>
     *
     * @param method the method
     * @return the int value
     */
    public static IntValue invokingMethod(final String method) {
        return new IntValue(method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int applyAsInt(final A value) {
        final Accessor<?> methodAcc = Annotator.of(value)
                                               .getAccessor(this.method);
        if (methodAcc == null) {
            return 0;
        } else {
            return (int) (Integer) methodAcc.get(value);
        }
    }
}
