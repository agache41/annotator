package com.orm.reflection.matcher;

import com.orm.reflection.annotator.Annotator;
import com.orm.reflection.matcher.base.Matcher;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.stream.Stream;

public class HaveAnnotation<M extends Annotation> implements Matcher<Object, M> {

    private final Class<M> annotationClass;

    public HaveAnnotation(Class<M> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public static final <M extends Annotation> HaveAnnotation<M> ofType(Class<M> annotation) {
        return new HaveAnnotation<>(annotation);
    }

    @Override
    public Stream<M> match(final Object value) {
        return (Stream<M>) Annotator
                .of(value)
                .getAnnotations()
                .filter(a -> Objects.equals(a
                        .annotationType(), this.annotationClass));
    }
}
