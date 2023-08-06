package com.orm.reflection.registry;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfiguredBy {
    Class<? extends Annotation> value();
}
