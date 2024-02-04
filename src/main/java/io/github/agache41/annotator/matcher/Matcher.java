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

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <pre>
 * The matcher interface defines the methods used for filtering annotations in selections.
 * </pre>
 *
 * @param <V> the type parameter of the reference value
 * @param <M> the type parameter of the resulting filtered matching values
 */
public interface Matcher<V, M> {

    /**
     * <pre>
     * Returns a stream of values from the scope that match the given value
     * </pre>
     *
     * @param value the reference value
     * @return the stream of matching values
     */
    Stream<M> match(V value);

    /**
     * <pre>
     * Tells if the current scope matches the given value
     * </pre>
     *
     * @param value the reference value
     * @return true if it matches, false otherwise
     */
    default boolean matches(final V value) {
        return this
                .match(value)
                .findAny()
                .isPresent();
    }

    /**
     * <pre>
     * Implements the logic or operator between thw Matchers
     * </pre>
     *
     * @param <R>     the type parameter
     * @param matcher the matcher to or with
     * @return the composed matcher
     */
    default <R> Matcher<V, Object> or(final Matcher<V, R> matcher) {
        return value -> Stream
                .concat(Matcher.this.match(value),
                        matcher.match(value))
                .distinct();
    }

    /**
     * <pre>
     * Implements the logic and operator between thw Matchers
     * </pre>
     *
     * @param <R>     the type parameter
     * @param matcher the matcher to and with
     * @return the composed matcher
     */
    default <R> Matcher<V, R> and(final Matcher<V, R> matcher) {
        return value -> {
            if (!Matcher.this.matches(value)) {
                return Stream.empty();
            }
            return matcher
                    .match(value);
        };
    }

    /**
     * <pre>
     * Implements the continuation of the matching process of the current matcher with the logic of the given parameter matcher.
     *
     * </pre>
     *
     * @param matcher the parameter matcher
     * @return the composed matcher
     */
    default Matcher<V, M> having(final Matcher<M, M> matcher) {
        return value -> Matcher.this
                .match(value)
                .filter(matcher::matches);
    }

    /**
     * <pre>
     * Implements the continuation of the matching process of the current matcher with the logic of the given parameter predicate.
     * </pre>
     *
     * @param predicate the predicate
     * @return the matcher
     */
    default Matcher<V, M> that(final Predicate<M> predicate) {
        return value -> Matcher.this
                .match(value)
                .filter(predicate::test);
    }

    /**
     * <pre>
     * Implements the continuation of the matching process of the current matcher with the logic of the given parameter predicate.
     * </pre>
     *
     * @param predicate the predicate
     * @return the matcher
     */
    default Matcher<V, M> where(final Predicate<M> predicate) {
        return value -> Matcher.this
                .match(value)
                .filter(predicate::test);
    }

    /**
     * <pre>
     * Implements the continuation of the matching process of the current matcher with the logic of the given parameter matcher.
     * </pre>
     *
     * @param matcher the matcher
     * @return the matcher
     */
    default Matcher<V, M> matching(final Matcher<M, M> matcher) {
        return value -> Matcher.this
                .match(value)
                .filter(matcher::matches);
    }
}
