package com.orm.reflection.annotator.base;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.matcher.base.Matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public interface Annotate<T> {

    boolean DEBUG = false;

    T get();

    Stream<Annotation> getAnnotations();

    <A extends Annotation> A getAnnotation(Class<A> annotationClass,
                                           boolean throwOnFailure);

    <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface,
                                                                boolean throwOnFailure);

    Stream<Field> getFields();

    Field getField(String name);

    Stream<Method> getMethods();

    Stream<Accessor<?>> getAccessors();

    Accessor<?> getAccessor(String name);

    String toString();

    default <A extends Annotation> boolean hasAnnotation(Class<A> clazz) {
        return this
                .getAnnotations(clazz)
                .findAny()
                .isPresent();
    }

    default <A extends Annotation> Stream<A> getAnnotations(Class<A> clazz) {
        return (Stream<A>) this
                .getAnnotations()
                .filter(annotation -> clazz
                        .equals(annotation
                                .annotationType()));
    }

    default Stream<Annotation> getAnnotationsThat(Matcher<Object, ?> matcher) {
        return this
                .getAnnotations()
                .filter(matcher::matches);
    }

    default Stream<Field> getFieldsThat(Matcher<Object, ?> matcher) {
        return this
                .getFields()
                .filter(matcher::matches);
    }

    default Stream<Method> getMethodsThat(Matcher<Object, ?> matcher) {
        return this
                .getMethods()
                .filter(matcher::matches);
    }

    default Stream<Accessor<?>> getAccessorsThat(Matcher<Object, ?> matcher) {
        return this
                .getAccessors()
                .filter(matcher::matches);
    }
}

