package com.orm.reflection.matcher;

import com.orm.annotations.Extends;
import com.orm.reflection.matcher.base.Matcher;

import java.util.stream.Stream;

public class AnExtendsValue implements Matcher<Extends, Extends> {

    private final Class<?> value;

    public AnExtendsValue(final Class<?> value) {
        this.value = value;
    }

    public static AnExtendsValue of(Class<?> value) {
        return new AnExtendsValue(value);
    }

    @Override
    public Stream<Extends> match(final Extends value) {
        if (Stream.of(value.value())
                  .filter(this.value::equals)
                  .count() == 0)
            return Stream.empty();
        else
            return Stream.of(value);

    }
}
