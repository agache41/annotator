package com.orm.temporary;

@TestAnnotation(name = "className")
public class TestBean {

    @TestAnnotation(name = "fieldName")
    private String nameWithAnnotation;

    private String nameWithoutAnnotation;

}
