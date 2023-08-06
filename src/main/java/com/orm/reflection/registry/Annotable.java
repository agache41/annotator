package com.orm.reflection.registry;

import com.orm.reflection.Helper;
import com.orm.reflection.accessor.Accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Annotable<A extends Annotation> {
    void configure(A cfg);

    default void configure(A cfg, Class<?> onClass, Field onField, Method onMethod, Accessor<?> accessor, String operation) {
        this.configure(cfg);
    }

    default String getAnnotationTypeName() {
        return Helper.getActualTypeNameForGenericInterfaceByIndex(this.getClass(), Annotable.class, 0);
    }
}
