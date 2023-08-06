package com.orm.temporary;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ClassAnnotator<T> extends AnnotatorB {

    private static final Map<Class<?>, ClassAnnotator<?>> concurrentClassDescriptorsCache = new ConcurrentHashMap<>();
    private final Map<String, FieldAnnotator> fieldAnnotators;

    private ClassAnnotator(Class<T> clazz) {
        super(clazz);
        //field descriptors
        this.fieldAnnotators = FieldUtils
                .getAllFieldsList(clazz)
                .stream()
                .map(field -> new FieldAnnotator(clazz, field))
                .collect(Collectors.toMap(FieldAnnotator::getName, Function.identity()));

        final Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("Annotation " + annotation.toString());
            Class<? extends Annotation> aclazz = annotation.annotationType();
            System.out.println("Class :" + aclazz.getName());
            for (Method field : aclazz.getDeclaredMethods()) {
                System.out.println("->method :" + field.getName());
            }
        }
    }

    /**
     * Given an object of a class, it returns the associated classdescriptor.
     * There will be just one class descriptor per class (Singleton)
     *
     * @param source
     * @return
     */
    public static ClassAnnotator ofClass(Object source) {
        return ofClass(source.getClass());
    }

    /**
     * Given an object of a class, it returns the associated classdescriptor.
     * There will be just one class descriptor per class (Singleton)
     *
     * @param clazz
     * @return
     */
    public static <R> ClassAnnotator<R> ofClass(Class<R> clazz) {
        return (ClassAnnotator<R>) concurrentClassDescriptorsCache.computeIfAbsent(clazz, ClassAnnotator::new);
    }

    public List<FieldAnnotator> getFieldDescriptorsWith(Class<? extends Annotation> annotation) {
        return this.fieldAnnotators
                .values()
                .stream()
                .filter(filter(annotation))
                .collect(Collectors.toList());
    }
}