package com.orm.temporary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatorB {

    private final Map<Class<? extends Annotation>, Annotation> annotations;
    private final String name;
    private final Class<?> type;

    public AnnotatorB(Class<?> clazz) {
        this.name = clazz.getSimpleName();
        this.type = clazz;
        this.annotations = new HashMap<>();
        //class and superclass annotations
        while (clazz != null && (!clazz.equals(Object.class))) {
            for (Annotation annotation : clazz.getAnnotations()) {
                this.annotations.put(annotation.annotationType(), annotation);
            }
            clazz = clazz.getSuperclass();
        }
    }

    public AnnotatorB(Field field) {
        this.name = field.getName();
        this.type = field.getType();
        this.annotations = Stream
                .of(field.getDeclaredAnnotations())
                .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
    }

    public AnnotatorB(Method method) {
        this.name = method.getName();
        this.type = method.getReturnType();
        this.annotations = Stream
                .of(method.getDeclaredAnnotations())
                .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
    }

    public static boolean isClassCollection(Class c) {
        return Collection.class.isAssignableFrom(c);
    }

    public static boolean isCollection(Object ob) {
        return ob != null && isClassCollection(ob.getClass());
    }

    public static boolean isMapCollection(Class c) {
        return Map.class.isAssignableFrom(c);
    }

    public static boolean isMap(Object ob) {
        return ob != null && isMapCollection(ob.getClass());
    }

    public static Predicate<AnnotatorB> filter(final Class<? extends Annotation> annotation) {
        return annotator -> annotator.contains(annotation);
    }

    public <A extends Annotation> A get(Class<A> annotation) {
        return (A) this.annotations.get(annotation);
    }

    public <A extends Annotation> boolean contains(Class<A> annotation) {
        return this.annotations.containsKey(annotation);
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return this.annotations;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
