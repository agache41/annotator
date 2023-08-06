package com.orm.temporary;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class FieldAnnotator<T> extends AnnotatorB {
    private static final String GETTER_PREFIX = "get";
    private static final String GETTER_PRIM_BOOL_PREFIX = "is";
    private static final String SETTER_PREFIX = "set";
    private static final Object[] NULL = new Object[]{null};
    private final Method readMethod;
    private final Method writeMethod;

    FieldAnnotator(Class<T> workingClass, Field field) {
        super(field);
        try {
            this.writeMethod = FieldAnnotator.getSetter(workingClass, field);
            this.readMethod = FieldAnnotator.getGetter(workingClass, field);
            //this.notNull = field.isAnnotationPresent(NotNull.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Method getGetter(Class<?> definingClass, Field field) {
        try {
            // the getter method to use
            return definingClass.getDeclaredMethod(
                    (boolean.class.equals(field.getType()) ?
                            GETTER_PRIM_BOOL_PREFIX :
                            GETTER_PREFIX) +
                            StringUtils.capitalize(field.getName()));
        } catch (SecurityException | NoSuchMethodException e) { // getter is faulty
            //throw new IllegalArgumentException(e.getMessage() + " when getting getter for " + field.getName() + " in class " + definingClass.getCanonicalName(), e);
            return null;
        }
    }

    public static Method getSetter(Class<?> definingClass, Field field) {
        try {
            // the setter method to use
            return definingClass.getDeclaredMethod(
                    SETTER_PREFIX +
                            StringUtils.capitalize(field.getName()),
                    field.getType());
        } catch (SecurityException | NoSuchMethodException e) { // setter is faulty
            //throw new IllegalArgumentException(e.getMessage() + " when getting setter for " + field.getName() + " in class " + definingClass.getCanonicalName(), e);
            return null;
        }
    }


}
