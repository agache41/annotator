package com.orm.reflection.predicate;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.Annotator;

import java.util.function.Predicate;

public class AreOnlyInView<M> implements Predicate<M> {

    private final String viewValue;

    public AreOnlyInView(final String viewValue) {
        this.viewValue = viewValue;
    }

    public static <M> AreOnlyInView<M> of(String viewValue) {
        return new AreOnlyInView(viewValue);
    }

    @Override
    public boolean test(final M m) {
        final Accessor<?> viewAcc = Annotator
                .of(m)
                .getAccessor("view");
        if (viewAcc == null)
            return false; // todo: add exception
        final Object actualValue = viewAcc.get(m);
        return this.viewValue.equals(actualValue);
    }
}
