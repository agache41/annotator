package com.orm.reflection.annotator;

import com.orm.annotations.Extends;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Extends(BaseTestClass.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestExtends {
}
