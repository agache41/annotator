package com.orm.reflection.accessor;

import com.orm.annotations.Position;
import com.orm.annotations.Recurse;
import com.orm.annotations.Recursiv;
import com.orm.reflection.Helper;
import com.orm.reflection.accessor.base.Positionable;
import com.orm.reflection.annotator.Annotator;
import com.orm.reflection.annotator.base.Annotate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Accessor<T> implements Positionable, Comparable<Positionable>, Member {
    public static final int NO_POSITION = -1;
    private static final Comparator<Positionable> FIELD_POSITION_COMPARATOR = new PositionComparator();
    private final Class<T> type;
    private final Type genericType;
    private final Class<?> declaringClass;
    private final Field field;
    private final Method getter;
    private final Method setter;
    private final List<Annotation> annotations;
    private final Set<Class<? extends Annotation>> annotationClasses;
    private final String name;
    private final Accessor<?> parent;
    private final Map<String, Accessor<?>> children;
    private final boolean leaf;
    private int position;
    private int level;

    //field constructor , used in class
    public Accessor(Class<T> type,
                    Class<?> declaringClass,
                    Field field) {
        this.type = type;
        this.genericType = field.getGenericType();
        this.declaringClass = declaringClass;
        this.field = field;
        this.parent = null;
        this.name = field.getName();
        this.getter = this.getter(declaringClass);
        this.setter = this.setter(declaringClass);
        this.annotations = this.annotations();
        this.annotationClasses = this.annotationClasses();
        this.position = this.position();
        this.level = 0;
        this.leaf = this.leaf();
        this.children = new HashMap<>();
        // if is a recurse
        if (!this.leaf)
            // let the children come to me
            this.associate(Annotator
                    .of(this.type)
                    .getAccessors()
                    .filter(Accessor::isAtRootLevel)
            );
    }

    // getter constructor, used in Annotations
    public Accessor(Class<T> type,
                    Class<?> declaringClass,
                    Method getter) {
        this.declaringClass = declaringClass;
        this.field = null;
        this.setter = null;
        this.type = type;
        this.genericType = getter.getGenericReturnType();
        this.name = getter.getName();
        this.getter = getter;
        this.annotations = this.annotations();
        this.annotationClasses = this.annotationClasses();
        this.parent = null;
        this.leaf = this.leaf();
        this.children = Collections.emptyMap();
    }

    //AllArgsConstructor - used in copy
    public Accessor(final Class<T> type,
                    final Type genericType,
                    final Class<?> declaringClass,
                    final Field field,
                    final Method getter,
                    final Method setter,
                    final List<Annotation> annotations,
                    final Set<Class<? extends Annotation>> annotationClasses,
                    final String name,
                    final int position,
                    final int level,
                    final Accessor<?> parent,
                    final boolean leaf,
                    Map<String, Accessor<?>> children) {
        this.type = type;
        this.genericType = genericType;
        this.declaringClass = declaringClass;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.annotations = annotations;
        this.annotationClasses = annotationClasses;
        this.name = name;
        this.position = position;
        this.level = level;
        this.parent = parent;
        this.leaf = leaf;
        this.children = children;
    }

    public static Comparator<Positionable> fieldPosition() {
        return FIELD_POSITION_COMPARATOR;
    }

    private Method setter(final Class<?> enclosingClass) {
        Method setter;
        try {
            setter = enclosingClass.getMethod(this.setterNameOf(this.name),
                    this.field.getType());
        } catch (NoSuchMethodException e) {
            setter = null;
        }
        return setter;
    }

    private Method getter(final Class<?> enclosingClass) {
        Method getter;
        try {
            getter = enclosingClass.getMethod(this.getterNameOf(this.name));
        } catch (NoSuchMethodException e) {
            getter = null;
        }
        return getter;
    }

    private String getterNameOf(String name) {
        return "get" + StringUtils.capitalize(name);
    }

    private String setterNameOf(String name) {
        return "set" + StringUtils.capitalize(name);
    }

    private List<Annotation> annotations() {
        List<Annotation> result = new LinkedList<>();
        if (this.field != null) result.addAll(Arrays.asList(this.field.getAnnotations()));
        if (this.setter != null) result.addAll(Arrays.asList(this.setter.getAnnotations()));
        if (this.getter != null) result.addAll(Arrays.asList(this.getter.getAnnotations()));
        return Helper.unpackAnnotations(result);
    }

    private Set<Class<? extends Annotation>> annotationClasses() {
        return this.annotations
                .stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
    }

    private int position() {
        return this.annotations
                .stream()
                .filter(annotation -> annotation
                        .annotationType()
                        .equals(Position.class))
                .map(annotation -> (Position) annotation)
                .map(position -> position.value())
                .findAny()
                .orElse(NO_POSITION);
    }

    public <V> V getAs(Object rootObject,
                       Class<V> expectingType) {
        return this.getAs(rootObject,
                expectingType,
                null);
    }

    public <V> V getAs(Object rootObject,
                       Class<V> expectingType,
                       V defaultValue) {
        Object objectValue = this.get(rootObject);
        if (objectValue == null) return defaultValue;
        if (!(expectingType.isAssignableFrom(objectValue.getClass()))) {
            throw new ClassCastException(this + " returns a " + objectValue.getClass()
                                                                           .getSimpleName() + " which can not be assigned to a " + expectingType.getSimpleName() + "!");
        }
        return (V) objectValue;
    }

    public Object get(Object rootObject) {
        Object levelObject = null;
        try {
            levelObject = this.levelObject(rootObject,
                    false);
            if (levelObject == null) return null;
            if (this.getter == null) {
                return this.field.get(levelObject);
            }
            return this.getter.invoke(levelObject);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object rootObject,
                    Object value) {
        Object levelObject = null;
        try {
            levelObject = this.levelObject(rootObject,
                    true);
            if (this.setter == null) {
                this.field.set(levelObject,
                        value);
                return;
            }
            this.setter.invoke(levelObject,
                    value);
        } catch (Exception e) {
            throw new RuntimeException(this.toString(),
                    e);
        }
    }

    private Object levelObject(Object rootObject,
                               boolean initializeIfNeeded) throws ReflectiveOperationException {
        Object levelObject;
        if (this.parent != null) {
            levelObject = this.parent.get(rootObject);
            if (levelObject == null && initializeIfNeeded) levelObject = this.parent.newInstance(rootObject);
            return levelObject;
        } else {
            return rootObject;
        }
    }

    public Object newInstance(Object rootObject) throws ReflectiveOperationException {
        Object initObject = this.type
                .getConstructor()
                .newInstance();
        this.set(rootObject,
                initObject);
        return initObject;
    }

    private boolean leaf() {
        return !this.annotationClasses.contains(Recurse.class) && !this.type.isAnnotationPresent(Recurse.class);
    }

    @Recursiv
    public Stream<Accessor<?>> expand() {
        if (this.leaf) {
            return Stream.of(this);
        }
        return Stream.concat(Stream.of(this),
                this.children
                        .values()
                        .stream()
                        .flatMap(Accessor::expand));
    }

    public Accessor<T> copy(Accessor<?> parent) {
        final Accessor<T> newMe = new Accessor<>(this.type,
                //
                this.genericType,
                //
                this.declaringClass,
                //
                this.field,
                //
                this.getter,
                //
                this.setter,
                //
                this.annotations,
                //
                this.annotationClasses,
                //
                parent.getName() + "." + this.field.getName(),
                //
                this.position,
                //
                // level is incremented
                // todo: add level limit
                parent.level + 1,
                //
                parent,
                //
                this.leaf,
                //
                new HashMap<>());
        if (!this.leaf) {
            // we copy the children
            newMe.associate(this.children
                    .values()
                    .stream()); //
        }
        if (Annotate.DEBUG) {
            System.out.println(" copy " + this.toTreeString() + "to" + newMe.toTreeString() + "for parent" + parent.toTreeString());
        }
        return newMe;
    }

    private void associate(Stream<Accessor<?>> children) {
        if (Annotate.DEBUG) {
            System.out.println("Associate children for:" + this.toTreeString());
        }
        children
                .map(acc -> acc.copy(this))
                .forEach(acc -> this.children.put(acc.name,
                        acc));
        if (Annotate.DEBUG) {
            System.out.println("Associated children for:" + this.toTreeString());
        }
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public Type getGenericType() {
        return this.genericType;
    }

    public Field getField() {
        return this.field;
    }

    public Method getGetter() {
        return this.getter;
    }

    public Method getSetter() {
        return this.setter;
    }

    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass,
                                                  boolean throwOnFailure) {
        A annotation = null;
        if (this.field != null) {
            annotation = this.field.getAnnotation(annotationClass);
        }
        if (annotation == null && this.getter != null) {
            annotation = this.getter.getAnnotation(annotationClass);
        }
        if (annotation == null && this.setter != null) {
            annotation = this.setter.getAnnotation(annotationClass);
        }
        if (throwOnFailure && annotation == null) {
            throw new RuntimeException("No annotations of type " + annotationClass.getSimpleName() + " where found on field " + this.field.getName());
        }
        return annotation;
    }

    public boolean isLeaf() {
        return this.leaf;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public boolean hasPosition() {
        return NO_POSITION != this.position;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public Accessor<?> getParent() {
        return this.parent;
    }

    @Override
    public boolean isAtRootLevel() {
        return this.level == 0;
    }

    //    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder(256);
//        sb.append(name);
//        sb.append("{");
//        printLPR(this, sb);
//        sb.append(" Class=");
//        sb.append(this.declaringClass.getSimpleName());
//        sb.append(".");
//        sb.append(this.type.getSimpleName());
//        sb.append(" Annotations=");
//        sb.append(this.annotations);
//        sb.append("}");
//        if (parent == null) {
//            sb.append("no parent");
//        } else {
//            sb.append("parent=" + parent.toString());
//        }
//        return sb.toString();
//    }
    @Override
    public String toString() {
        return this.declaringClass.getSimpleName() + ".acc." + this.getName();
    }

    public String toTreeString() {
        return this.toTreeString("");
    }

    @Recursiv
    private String toTreeString(String prefix) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("\n");
        sb.append(prefix);
        sb.append(this.name);
        if (this.parent == null) {
            sb.append("(no parent)");
        } else {
            sb.append("(parent=");
            sb.append(this.parent.name);
            sb.append(")");
        }

        final String childPrefix = prefix.equals("") ? "\\---" : "    " + prefix;
        this.children
                .values()
                .stream()
                .forEach(child -> {
                    sb.append(child.toTreeString(childPrefix));
                });
        return sb.toString();
    }

    @Recursiv
    private void printLPR(Accessor<?> accessor,
                          StringBuilder sb) {
        if (accessor == null) {
            sb.append("root");
            return;
        }
        this.printLPR(accessor.parent,
                sb);
        sb.append("\\");
        sb.append("L");
        sb.append(accessor.level);
        sb.append("P");
        sb.append(accessor.position);
        sb.append("R");
        sb.append(accessor.leaf ? "1" : "0");
    }

    @Override
    public int compareTo(Positionable pos2) {
        return FIELD_POSITION_COMPARATOR.compare(this,
                pos2);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        final Accessor<?> accessor = (Accessor<?>) o;

        return new EqualsBuilder()
                .append(this.type,
                        accessor.type)
                .append(this.declaringClass,
                        accessor.declaringClass)
                .append(this.field,
                        accessor.field)
                .append(this.getter,
                        accessor.getter)
                .append(this.setter,
                        accessor.setter)
                .append(this.annotationClasses,
                        accessor.annotationClasses)
                .append(this.name,
                        accessor.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,
                37)
                .append(this.type)
                .append(this.declaringClass)
                .append(this.field)
                .append(this.getter)
                .append(this.setter)
                .append(this.annotationClasses)
                .append(this.name)
                .toHashCode();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    @Override
    public int getModifiers() {
        return this.field.getModifiers();
    }

    @Override
    public boolean isSynthetic() {
        return true;
    }
}
