package com.orm.reflection.annotator.annotators;

import com.orm.reflection.Helper;
import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.base.Annotate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassAnnotator<T> implements Annotate<Class<T>> {

    private static final Map<Class<?>, ClassAnnotator<?>> classAnnotatorMap = new ConcurrentHashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<String, Accessor<?>> accessors = new HashMap<>();
    private final Set<Method> methods = new HashSet<>();
    private final List<Annotation> annotations = new ArrayList<>();
    private final Class<T> clazz;

    private ClassAnnotator(Class<T> clazz) {
        this.clazz = clazz;
        Class<?> classType = clazz;
        while (classType != null && (!classType.equals(Object.class))) {
            for (Field field : classType.getDeclaredFields()) {
                this.fields.put(field.getName(), field);
            }
            Collections.addAll(this.methods, classType.getDeclaredMethods());
            this.annotations.addAll(Helper.unpackAnnotations(Stream.of(classType.getDeclaredAnnotations())).collect(Collectors.toList()));
            classType = classType.getSuperclass();
        }
        if (!this.clazz.isAnnotation()) {
            this.fields
                    .keySet()
                    .stream()
                    .map(fieldName -> new Accessor<>(  //
                            this.fields.get(fieldName) //
                                       .getType(),
                            this.clazz,//
                            this.fields.get(fieldName)))//
                    .flatMap(Accessor::expand)
                    .forEach(accessor -> this.accessors.put(accessor.getName(), accessor));
        } else {
            this.methods
                    .stream()
                    .map(method -> new Accessor<>(method.getReturnType(), this.clazz, method))
                    .flatMap(Accessor::expand)
                    .forEach(accessor -> this.accessors.put(accessor.getName(), accessor));
        }
    }

    public static synchronized <T> ClassAnnotator of(Class<T> clazz) {
        //todo: refactor all like this
        ClassAnnotator<?> classAnnotator = classAnnotatorMap.get(clazz);
        if (classAnnotator == null) {
            classAnnotator = new ClassAnnotator<>(clazz);
            classAnnotatorMap.put(clazz, classAnnotator);
        }
        return classAnnotator;
        //return classAnnotatorMap.computeIfAbsent(clazz, ClassAnnotator::new);
    }

    @Override
    public Class<T> get() {
        return this.clazz;
    }

    @Override
    public Stream<Annotation> getAnnotations() {
        return this.annotations.stream();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass, boolean throwOnFailure) {
        A annotation = this.clazz.getAnnotation(annotationClass);
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found on class " + this.clazz.getSimpleName());
        }
        return annotation;
    }

    @Override
    //todo: generalize
    public <I> Class<?>[] getParameterizedTypesForImplementedInterface(Class<I> implementedInterface, boolean throwOnFailure) {
        Class[] classes = this.clazz.getInterfaces();
        int index = -1;
        for (int i = 0; i < classes.length; i++) {
            if (implementedInterface.equals(classes[i])) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new RuntimeException(" Interface " + implementedInterface.getSimpleName() + " was not implemented in class" + this.clazz.getSimpleName() + "!");
        }
        ParameterizedType parameterizedType = (ParameterizedType) this.clazz.getGenericInterfaces()[index];
        return (Class<?>[]) parameterizedType.getActualTypeArguments();
    }

    @Override
    public Stream<Field> getFields() {
        return this.fields
                .values()
                .stream();
    }

    @Override
    public Field getField(final String name) {
        return this.fields.get(name);
    }

    @Override
    public Stream<Method> getMethods() {
        return this.methods.stream();
    }

    @Override
    public Stream<Accessor<?>> getAccessors() {
        return this.accessors
                .values()
                .stream();
    }

    @Override
    public Accessor<?> getAccessor(String name) {
        if (!this.accessors.containsKey(name)) {
            throw new IllegalArgumentException("No such method or field " + name + " in " + this.clazz.getSimpleName() + "!");
        }
        return this.accessors.get(name);
    }

    @Override
    public String toString() {
//        StringBuilder sb = new StringBuilder(256);
//        sb.append("ClassAnnotator{");
//        sb.append(this.clazz.getName());
//        sb.append("={\n\t\t");
//        sb.append(this.accessors
//                .values()
//                .stream()
//                .map(Accessor::toString)
//                .collect(Collectors.joining(",\n\t\t")));
//        sb.append("\r\n");
//        sb.append("Annotations=");
//        sb.append(this.annotations);
//        sb.append("}");
//        return sb.toString();
        return "Class<" + this.clazz.getSimpleName() + ">";
    }
}
