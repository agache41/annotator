package com.orm.reflection.comparator;

import java.lang.annotation.Annotation;
import java.util.Comparator;

public class AnnotationComparator implements Comparator<Annotation> {
    private final String method;

    public AnnotationComparator(final String method) {
        this.method = method;
    }

    public static AnnotationComparator invokingMethod(final String method) {
        return new AnnotationComparator(method);
    }

    @Override
    public int compare(Annotation o1, Annotation o2) {
        return 0;
    }
}
