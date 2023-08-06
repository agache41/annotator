package com.orm.annotations;

import java.lang.annotation.*;

@Inherited
@Repeatable(Positions.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Position {
    int value();
}
