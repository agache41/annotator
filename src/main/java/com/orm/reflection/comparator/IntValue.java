package com.orm.reflection.comparator;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.Annotator;

import java.util.function.ToIntFunction;

public class IntValue<A> implements ToIntFunction<A> {
    private final String method;
    int value;

    public IntValue(final String method) {
        this.method = method;
    }

    public static IntValue invokingMethod(String method) {
        return new IntValue(method);
    }

    @Override
    public int applyAsInt(A value) {
        Accessor<?> methodAcc = Annotator.of(value)
                                         .getAccessor(this.method);
        if (methodAcc == null) return 0;
        else return (int) (Integer) methodAcc.get(value);
    }
}
