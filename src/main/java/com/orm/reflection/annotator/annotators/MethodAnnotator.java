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

public class MethodAnnotator implements Annotate<Method> {
    private static final Map<Method, MethodAnnotator> methodAnnotatorMap = new ConcurrentHashMap<>();
    private final Method method;

    private MethodAnnotator(Method method) {
        this.method = method;
    }

    public static MethodAnnotator of(Method method) {
        return methodAnnotatorMap.computeIfAbsent(method, MethodAnnotator::new);
    }

    @Override
    public Method get() {
        return this.method;
    }

    @Override
    public Stream<Annotation> getAnnotations() {
        return Helper.unpackAnnotations(Stream.of(this.method.getAnnotations()));
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass, boolean throwOnFailure) {
        A annotation = this.method.getAnnotation(annotationClass);
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found in method " + this.method.getName());
        }
        return annotation;
    }

    @Override
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface, boolean throwOnFailure) {
        throw new RuntimeException(" Methods do not have implemented interfaces!");
    }

    @Override
    public Stream<Field> getFields() {

        throw new IllegalStateException(" Methods do not have fields!");
    }

    @Override
    public Field getField(final String name) {
        throw new IllegalStateException(" Methods do not have fields!");
    }

    @Override
    public Stream<Method> getMethods() {
        throw new IllegalStateException(" Methods do not have methods!");
    }

    @Override
    public Stream<Accessor<?>> getAccessors() {

        throw new IllegalStateException(" Methods do not have accessors!");
    }

    @Override
    public Accessor<Field> getAccessor(String name) {
        throw new IllegalStateException(" Methods do not have accessors!");
    }

    @Override
    public String toString() {
        return this.method.getDeclaringClass()
                          .getSimpleName() + "." + this.method.getName() + "(...)";
    }
}
