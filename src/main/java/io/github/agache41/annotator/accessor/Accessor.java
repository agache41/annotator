/*
 *    Copyright 2022-2023  Alexandru Agache
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.agache41.annotator.accessor;

import io.github.agache41.annotator.Helper;
import io.github.agache41.annotator.annotations.Position;
import io.github.agache41.annotator.annotations.Recurse;
import io.github.agache41.annotator.annotations.Recursive;
import io.github.agache41.annotator.annotator.Annotate;
import io.github.agache41.annotator.annotator.Annotator;
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

/**
 * <pre>
 * The Accessor class implement the pattern of accessing a class property in a unified way.
 * A property can consist in a value accessible by means of a field or getter/setter methods.
 * The class also provides method for handling annotations in this context.
 * Example of usage consist in
 * - getting the getter and  setter
 * - getting the annotations
 * - test specific properties of a field or method
 * Its main purpose is to ease the "access" to a class property , field or setter /getter.
 * </pre>
 *
 * @param <T> the type of class containing the accessed field.
 */
public class Accessor<T> implements Positionable, Comparable<Positionable>, Member {
    /**
     * <pre>
     * The constant NO_POSITION.
     * </pre>
     */
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

    /**
     * <pre>
     * Instantiates a new Accessor.
     * This constructor is used in class instantiation.
     * </pre>
     *
     * @param type           the type
     * @param declaringClass the declaring class
     * @param field          the field
     */
    public Accessor(final Class<T> type,
                    final Class<?> declaringClass,
                    final Field field) {
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
        {
            this.associate(Annotator
                                   .of(this.type)
                                   .getAccessors()
                                   .filter(Accessor::isAtRootLevel)
            );
        }
    }

    /**
     * <pre>
     * Instantiates a new Accessor.
     * This constructor is used in annotation instantiation.
     * </pre>
     *
     * @param type           the type
     * @param declaringClass the declaring class
     * @param getter         the getter
     */
    public Accessor(final Class<T> type,
                    final Class<?> declaringClass,
                    final Method getter) {
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

    /**
     * <pre>
     * Instantiates a new Accessor based on an existing one.
     * Copy Constructor.
     * </pre>
     *
     * @param type              the type
     * @param genericType       the generic type
     * @param declaringClass    the declaring class
     * @param field             the field
     * @param getter            the getter
     * @param setter            the setter
     * @param annotations       the annotations
     * @param annotationClasses the annotation classes
     * @param name              the name
     * @param position          the position
     * @param level             the level
     * @param parent            the parent
     * @param leaf              the leaf
     * @param children          the children
     */
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
                    final Map<String, Accessor<?>> children) {
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

    /**
     * <pre>
     * Field position comparator.
     * </pre>
     *
     * @return the comparator
     */
    public static Comparator<Positionable> fieldPosition() {
        return FIELD_POSITION_COMPARATOR;
    }

    private static String getterNameOf(final String name) {
        return "get" + StringUtils.capitalize(name);
    }

    private static String setterNameOf(final String name) {
        return "set" + StringUtils.capitalize(name);
    }

    private Method setter(final Class<?> enclosingClass) {
        Method setter;
        try {
            setter = enclosingClass.getMethod(Accessor.setterNameOf(this.name),
                                              this.field.getType());
        } catch (final NoSuchMethodException e) {
            setter = null;
        }
        return setter;
    }

    private Method getter(final Class<?> enclosingClass) {
        Method getter;
        try {
            getter = enclosingClass.getMethod(Accessor.getterNameOf(this.name));
        } catch (final NoSuchMethodException e) {
            getter = null;
        }
        return getter;
    }

    private List<Annotation> annotations() {
        final List<Annotation> result = new LinkedList<>();
        if (this.field != null) {
            result.addAll(Arrays.asList(this.field.getAnnotations()));
        }
        if (this.setter != null) {
            result.addAll(Arrays.asList(this.setter.getAnnotations()));
        }
        if (this.getter != null) {
            result.addAll(Arrays.asList(this.getter.getAnnotations()));
        }
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

    /**
     * <pre>
     * Gets the value as the given type.
     * </pre>
     *
     * @param <V>           the type parameter
     * @param rootObject    the root object
     * @param expectingType the expecting type
     * @return the value as the given type
     */
    public <V> V getAs(final Object rootObject,
                       final Class<V> expectingType) {
        return this.getAs(rootObject,
                          expectingType,
                          null);
    }

    /**
     * <pre>
     * Gets the value as the given type or returns the default value.
     * </pre>
     *
     * @param <V>           the type parameter
     * @param rootObject    the root object
     * @param expectingType the expecting type
     * @param defaultValue  the default value
     * @return the value as the given type
     */
    public <V> V getAs(final Object rootObject,
                       final Class<V> expectingType,
                       final V defaultValue) {
        final Object objectValue = this.get(rootObject);
        if (objectValue == null) {
            return defaultValue;
        }
        if (!(expectingType.isAssignableFrom(objectValue.getClass()))) {
            throw new ClassCastException(this + " returns a " + objectValue.getClass()
                                                                           .getSimpleName() + " which can not be assigned to a " + expectingType.getSimpleName() + "!");
        }
        return (V) objectValue;
    }

    /**
     * <pre>
     * Gets the value from this accessor, applying it on the given rootObject.
     * </pre>
     *
     * @param rootObject the root object
     * @return the object
     */
    public Object get(final Object rootObject) {
        Object levelObject = null;
        try {
            levelObject = this.levelObject(rootObject,
                                           false);
            if (levelObject == null) {
                return null;
            }
            if (this.getter == null) {
                return this.field.get(levelObject);
            }
            return this.getter.invoke(levelObject);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>
     *  Gets the value through this accessor, applying it on the given rootObject.
     * </pre>
     *
     * @param rootObject the root object
     * @param value      the value
     */
    public void set(final Object rootObject,
                    final Object value) {
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
        } catch (final Exception e) {
            throw new RuntimeException(this.toString(),
                                       e);
        }
    }

    private Object levelObject(final Object rootObject,
                               final boolean initializeIfNeeded) throws ReflectiveOperationException {
        Object levelObject;
        if (this.parent != null) {
            levelObject = this.parent.get(rootObject);
            if (levelObject == null && initializeIfNeeded) {
                levelObject = this.parent.newInstance(rootObject);
            }
            return levelObject;
        } else {
            return rootObject;
        }
    }

    /**
     * <pre>
     * Creates a new instance of the underlining value of this accessor,applying it on the given rootObject.
     * </pre>
     *
     * @param rootObject the root object
     * @return the object
     * @throws ReflectiveOperationException the reflective operation exception
     */
    public Object newInstance(final Object rootObject) throws ReflectiveOperationException {
        final Object initObject = this.type
                .getConstructor()
                .newInstance();
        this.set(rootObject,
                 initObject);
        return initObject;
    }

    private boolean leaf() {
        return !this.annotationClasses.contains(Recurse.class) && !this.type.isAnnotationPresent(Recurse.class);
    }

    /**
     * <pre>
     * Expands the value stream, incorporating the children of each element.
     * </pre>
     *
     * @return the stream
     */
    @Recursive
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

    /**
     * <pre>
     * Copy the given accessor.
     * </pre>
     *
     * @param parent the parent
     * @return the accessor copy.
     */
    public Accessor<T> copy(final Accessor<?> parent) {
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

    private void associate(final Stream<Accessor<?>> children) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * <pre>
     * Gets the type.
     * </pre>
     *
     * @return the type
     */
    public Class<T> getType() {
        return this.type;
    }

    /**
     * <pre>
     * Gets the generic type.
     * </pre>
     *
     * @return the generic type
     */
    public Type getGenericType() {
        return this.genericType;
    }

    /**
     * <pre>
     * Gets the field.
     * </pre>
     *
     * @return the field
     */
    public Field getField() {
        return this.field;
    }

    /**
     * <pre>
     * Gets the getter.
     * </pre>
     *
     * @return the getter
     */
    public Method getGetter() {
        return this.getter;
    }

    /**
     * <pre>
     * Gets the setter.
     * </pre>
     *
     * @return the setter
     */
    public Method getSetter() {
        return this.setter;
    }

    /**
     * <pre>
     * Gets the annotations on the underlining field or getter / setter
     * </pre>
     *
     * @return the annotations
     */
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * <pre>
     * Gets annotation of a specific type-
     * </pre>
     *
     * @param <A>             the type parameter
     * @param annotationClass the annotation class
     * @param throwOnFailure  the throw on failure
     * @return the annotation
     */
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass,
                                                  final boolean throwOnFailure) {
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

    /**
     * <pre>
     * Tells if this field is a end of the tree, and does not expand anymore
     * </pre>
     *
     * @return the boolean
     */
    public boolean isLeaf() {
        return this.leaf;
    }

    /**
     * <pre>
     * Gets the position of this accessor.
     * </pre>
     *
     * @return the position
     */
    @Override
    public int getPosition() {
        return this.position;
    }

    /**
     * <pre>
     * Tells if this field has a position.
     * </pre>
     *
     * @return the boolean
     */
    @Override
    public boolean hasPosition() {
        return NO_POSITION != this.position;
    }

    /**
     * <pre>
     * Gets the level of this accessor.
     * </pre>
     *
     * @return the level
     */
    @Override
    public int getLevel() {
        return this.level;
    }

    /**
     * <pre>
     * Sets the level of this accessor.
     * </pre>
     *
     * @param level the level
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * <pre>
     * Gets the parent of this accessor.
     * </pre>
     *
     * @return the parent
     */
    @Override
    public Accessor<?> getParent() {
        return this.parent;
    }

    /**
     * <pre>
     * Tells if this field is located at root level.
     * </pre>
     *
     * @return true if this field is at root level, false otherwise
     */
    @Override
    public boolean isAtRootLevel() {
        return this.level == 0;
    }

    /**
     * <pre>
     * The String representation
     * </pre>
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.declaringClass.getSimpleName() + ".acc." + this.getName();
    }

    /**
     * <pre>
     * To tree string string.
     * </pre>
     *
     * @return the string
     */
    public String toTreeString() {
        return this.toTreeString("");
    }

    @Recursive
    private String toTreeString(final String prefix) {
        final StringBuilder sb = new StringBuilder(256);
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

    @Recursive
    private void printLPR(final Accessor<?> accessor,
                          final StringBuilder sb) {
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

    /**
     * <pre>
     * Implementation of the Comparable.compareTo method
     * @see Comparable
     * </pre>
     *
     * @param pos2 the pos 2
     * @return the comparison result
     */
    @Override
    public int compareTo(final Positionable pos2) {
        return FIELD_POSITION_COMPARATOR.compare(this,
                                                 pos2);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        //todo: optimize this by saving the hash value.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return this.field.getModifiers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSynthetic() {
        return true;
    }
}
