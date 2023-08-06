package com.orm.reflection.annotator;

import com.orm.annotations.Recurse;

@TestExtends
public class MarkedClass {

    @TestExtends
    private String annotatedField;
    private String notAnnotatedField;

    @TestExtendsWithValue("myValue")
    private String annotatedWithValue;

    @TestExtends
    @TestExtendsWithValue("myBothValue")
    private String annotatedWithBoth;
    @Recurse
    private InnerClass innerClass;


    @Multiple("one")
    @Multiple("two")
    private String multipleAnnotation;

    @TestExtends
    public String getAnnotatedField() {
        return this.annotatedField;
    }

    public void setAnnotatedField(final String annotatedField) {
        this.annotatedField = annotatedField;
    }

    public String getNotAnnotatedField() {
        return this.notAnnotatedField;
    }

    public void setNotAnnotatedField(final String notAnnotatedField) {
        this.notAnnotatedField = notAnnotatedField;
    }
}
