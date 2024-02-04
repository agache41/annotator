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
 * Reference value for matching a specific predicate
 * Example :
 * Annotator.of(new MarkedClass())
 *          .getAnnotationsThat(Match.the(predicate))
 * </pre>
 *
 * @param <M> the type parameter
 */
public class Match<M> implements Matcher<M, M> {
    private final Predicate<M> predicate;

    /**
     * <pre>
     * Instantiates a new Match based on the given predicate.
     * </pre>
     *
     * @param predicate the predicate
     */
    public Match(final Predicate<M> predicate) {
        this.predicate = predicate;
    }

    /**
     * <pre>
     * Static provider method used to create a Match based on the given predicate.
     * </pre>
     *
     * @param <M>       the type parameter
     * @param predicate the predicate
     * @return the match
     */
    public static <M> Match<M> the(final Predicate<M> predicate) {
        return new Match(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<M> match(final M value) {
        if (this.predicate.test(value)) {
            return Stream.of(value);
        } else {
            return Stream.empty();
        }
    }
}
