package com.orm.reflection.matcher;

import com.orm.reflection.matcher.base.Matcher;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class Match<M> implements Matcher<M, M> {
    private final Predicate<M> predicate;

    public Match(final Predicate<M> predicate) {
        this.predicate = predicate;
    }

    public static <M> Match<M> the(Predicate<M> predicate) {
        return new Match(predicate);
    }

    @Override
    public Stream<M> match(final M value) {
        if (this.predicate.test(value))
            return Stream.of(value);
        else return Stream.empty();
    }
}
