package com.orm.reflection.annotator.annotators;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.base.Annotate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class AccessorAnnotator implements Annotate<Accessor<?>> {

    private static final Map<Accessor<?>, AccessorAnnotator> accessorAnnotatorMap = new ConcurrentHashMap<>();
    private final Accessor<?> accessor;

    private AccessorAnnotator(Accessor<?> accessor) {
        this.accessor = accessor;
    }

    public static AccessorAnnotator of(Accessor<?> field) {
        return accessorAnnotatorMap.computeIfAbsent(field, AccessorAnnotator::new);
    }

    @Override
    public Accessor<?> get() {
        return this.accessor;
    }

    @Override
    public Stream<Annotation> getAnnotations() {
        return this.accessor
                .getAnnotations()
                .stream();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass, boolean throwOnFailure) {
        return this.accessor.getAnnotation(annotationClass, throwOnFailure);
    }

    @Override
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface, boolean throwOnFailure) {
        throw new RuntimeException(" Fields or Methods do not have implemented interfaces!");
    }

    @Override
    public Stream<Field> getFields() {

        throw new IllegalStateException(" Accessors do not have fields!");
    }

    @Override
    public Field getField(final String name) {
        throw new IllegalStateException(" Accessors do not have fields!");
    }

    @Override
    public Stream<Method> getMethods() {
        throw new IllegalStateException(" Accessors do not have methods!");
    }

    @Override
    public Stream<Accessor<?>> getAccessors() {

        throw new IllegalStateException(" Accessors do not have accessors!");
    }

    @Override
    public Accessor<Field> getAccessor(String name) {
        throw new IllegalStateException(" Accessors do not have accessors!");
    }

    @Override
    public String toString() {
        return this.accessor.getDeclaringClass()
                            .getSimpleName() + ":acc:" + this.accessor.getName();
    }
}
