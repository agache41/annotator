package com.orm.reflection.annotator.annotators;

import com.orm.reflection.Helper;
import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.base.Annotate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class FieldAnnotator implements Annotate<Field> {

    private static final Map<Field, FieldAnnotator> fieldAnnotatorMap = new ConcurrentHashMap<>();
    private final Field field;

    private FieldAnnotator(Field field) {
        this.field = field;
    }

    public static FieldAnnotator of(Field field) {
        return fieldAnnotatorMap.computeIfAbsent(field, FieldAnnotator::new);
    }

    @Override
    public Field get() {
        return this.field;
    }

    @Override
    public Stream<Annotation> getAnnotations() {
        return Helper.unpackAnnotations(Stream.of(this.field.getAnnotations()));
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass, boolean throwOnFailure) {
        A annotation = this.field.getAnnotation(annotationClass);
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found on field " + this.field.getName());
        }
        return annotation;
    }

    @Override
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface, boolean throwOnFailure) {
        throw new IllegalStateException(" Fields do not have implemented interfaces!");
    }

    @Override
    public Stream<Field> getFields() {

        throw new IllegalStateException(" Fields do not have fields!");
    }

    @Override
    public Field getField(final String name) {
        throw new IllegalStateException(" Fields do not have fields!");
    }

    @Override
    public Stream<Method> getMethods() {
        throw new IllegalStateException(" Fields do not have methods!");
    }

    @Override
    public Stream<Accessor<?>> getAccessors() {
        throw new IllegalStateException(" Fields do not have accessors!");
    }

    @Override
    public Accessor<Field> getAccessor(String name) {
        throw new IllegalStateException(" Fields do not have accessors!");
    }

    @Override
    public String toString() {
        return this.field.getDeclaringClass()
                         .getSimpleName() + "." + this.field.getName();
    }
}
