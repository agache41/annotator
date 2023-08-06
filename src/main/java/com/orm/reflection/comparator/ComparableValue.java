package com.orm.reflection.comparator;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.Annotator;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public class ComparableValue<T extends Comparable<? super T>> implements Function<Annotation, T> {
    private final String method;

    public ComparableValue(final String method) {
        this.method = method;
    }

    public static <R extends Comparable<? super R>> ComparableValue<R> invokingMethod(final String method) {
        return new ComparableValue(method);
    }

    @Override
    public T apply(Annotation value) {
        T result = null;
        Accessor<?> methodAcc = Annotator.of(value)
                                         .getAccessor(this.method);
        if (methodAcc != null) return (T) methodAcc.get(value);
        throw new IllegalArgumentException(" No such method [" + this.method + "] in " + value);
    }
}
