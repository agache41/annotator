package com.orm.reflection.predicate;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.Annotator;

import java.util.function.Predicate;

import static com.orm.annotations.Annotations.DEFAULT;

public class AreInDefaultOrInView<M> implements Predicate<M> {

    private final String viewValue;

    public AreInDefaultOrInView(final String viewValue) {
        this.viewValue = viewValue;
    }

    public static <M> AreInDefaultOrInView<M> of(String viewValue) {
        return new AreInDefaultOrInView(viewValue);
    }

    @Override
    public boolean test(final M m) {
        final Accessor<?> viewAcc = Annotator
                .of(m)
                .getAccessor("view");
        if (viewAcc == null)
            return true; // if the Annotation does not have the view field, it will be automatically allowed.
        final Object actualValue = viewAcc.get(m);
        return this.viewValue.equals(actualValue) || DEFAULT.equalsIgnoreCase(actualValue.toString());
    }
}
