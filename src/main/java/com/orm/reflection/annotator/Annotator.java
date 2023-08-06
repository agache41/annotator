package com.orm.reflection.annotator;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.annotators.AccessorAnnotator;
import com.orm.reflection.annotator.annotators.ClassAnnotator;
import com.orm.reflection.annotator.annotators.FieldAnnotator;
import com.orm.reflection.annotator.annotators.MethodAnnotator;
import com.orm.reflection.annotator.base.Annotate;
import com.orm.reflection.predicate.AreInDefaultOrInView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class Annotator {
    public static final <T> Annotate<T> of(T value) {
        if (value instanceof Accessor) {
            return (Annotate<T>) AccessorAnnotator.of((Accessor<T>) value);
        }
        if (value instanceof Field) {
            return (Annotate<T>) FieldAnnotator.of((Field) value);
        }
        if (value instanceof Method) {
            return (Annotate<T>) MethodAnnotator.of((Method) value);
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            return ClassAnnotator.of(annotation.annotationType());
        }
        if (value instanceof Class) {
            Class<?> clazz = (Class<?>) value;
            return ClassAnnotator.of(clazz);
        }
        return ClassAnnotator.of(value.getClass());
    }
    //todo : addMethods for every Type

    public static <M extends Annotation, T> M getAnnotationForView(final T value, Class<M> annotationClass, String view) {
        return getAnnotationForView(value, annotationClass, view, false);
    }

    public static <M extends Annotation, T> M getAnnotationForView(final T value, Class<M> annotationClass, String view, boolean throwOnFailure) {
        final List<M> collect = Annotator
                .of(value)
                .getAnnotations(annotationClass)
                .filter(AreInDefaultOrInView.of(view))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            if (throwOnFailure) {
                throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found in default or " + view + " view on " + value.toString());
            }
            return null;
        }
        if (collect.size() > 1) {
            throw new RuntimeException("More than one annotation of type " + annotationClass.getSimpleName() + " where found in default or " + view + " view on " + value.toString());
        }
        return collect.get(0);
    }
}
