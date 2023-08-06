package com.orm.reflection.matcher.base;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Matcher<V, M> {

    Stream<M> match(V value);

    default boolean matches(V value) {
        return this
                .match(value)
                .findAny()
                .isPresent();
    }

    default <R> Matcher<V, Object> or(final Matcher<V, R> matcher) {
        return value -> Stream
                .concat(Matcher.this.match(value),
                        matcher.match(value))
                .distinct();
    }

    default <R> Matcher<V, R> and(Matcher<V, R> matcher) {
        return value -> {
            if (!Matcher.this.matches(value))
                return Stream.empty();
            return matcher
                    .match(value);
        };
    }

    default Matcher<V, M> having(Matcher<M, M> matcher) {
        return value -> Matcher.this
                .match(value)
                .filter(matcher::matches);
    }

    default Matcher<V, M> that(Predicate<M> predicate) {
        return value -> Matcher.this
                .match(value)
                .filter(predicate::test);
    }

    default Matcher<V, M> where(Predicate<M> predicate) {
        return value -> Matcher.this
                .match(value)
                .filter(predicate::test);
    }

    default Matcher<V, M> matching(Matcher<M, M> matcher) {
        return value -> Matcher.this
                .match(value)
                .filter(matcher::matches);
    }
}
