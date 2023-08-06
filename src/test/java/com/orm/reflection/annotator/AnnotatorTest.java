package com.orm.reflection.annotator;

import com.orm.annotations.Extends;
import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.matcher.AnExtendsValue;
import com.orm.reflection.matcher.HaveAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AnnotatorTest {

    @Test
    public void testAnnotations() {
        final List<Annotation> annotations = Annotator
                .of(new MarkedClass())
                .getAnnotations()
                .collect(Collectors.toList());

        Assertions.assertEquals(1, annotations.size());
        Annotation annot = annotations.get(0);
        Assertions.assertNotNull(annot);
        Assertions.assertEquals(TestExtends.class, annot.annotationType());
    }

    @Test
    public void testAnnotationsForClass() {
        final List<TestExtends> annotations = Annotator
                .of(new MarkedClass())
                .getAnnotations(TestExtends.class)
                .collect(Collectors.toList());

        Assertions.assertEquals(1, annotations.size());
        Annotation annot = annotations.get(0);
        Assertions.assertNotNull(annot);
        Assertions.assertEquals(TestExtends.class, annot.annotationType());
    }

    @Test
    public void testAnnotationHavingExtends() {
        final List<Annotation> annotations = Annotator
                .of(new MarkedClass())
                .getAnnotationsThat(HaveAnnotation
                        .ofType(Extends.class)
                        //.matching(anExtends -> Extends.class.equals(anExtends.value())));
                        .having(AnExtendsValue.of(BaseTestClass.class)))
                .collect(Collectors.toList());

        Assertions.assertEquals(1, annotations.size());
        Annotation annot = annotations.get(0);
        Assertions.assertNotNull(annot);
        Assertions.assertEquals(TestExtends.class, annot.annotationType());
    }

    @Test
    public void testAnnotationThatHaveExtends() {
        final List<Annotation> annotations = Annotator
                .of(new MarkedClass())
                .getAnnotationsThat(HaveAnnotation
                        .ofType(Extends.class)
                        .where(anExtends -> Stream.of(anExtends
                                                          .value())
                                                  .filter(v -> v.equals(BaseTestClass.class))
                                                  .count() > 0))
                .collect(Collectors.toList());

        Assertions.assertEquals(1, annotations.size());
        Annotation annot = annotations.get(0);
        Assertions.assertNotNull(annot);
        Assertions.assertEquals(TestExtends.class, annot.annotationType());
    }

    @Test
    public void testFieldsThatHaveTestExtends() {
        final List<Field> fields = Annotator
                .of(new MarkedClass())
                .getFieldsThat(HaveAnnotation
                        .ofType(TestExtends.class))
                .collect(Collectors.toList());

        Assertions.assertEquals(2, fields.size());
        Field field = fields.get(0);
        Assertions.assertNotNull(field);
        Assertions.assertEquals(String.class, field.getType());
    }

    @Test
    public void testMethodsThatHaveTestExtends() {
        final List<Method> methods = Annotator
                .of(new MarkedClass())
                .getMethodsThat(HaveAnnotation
                        .ofType(TestExtends.class))
                .collect(Collectors.toList());

        Assertions.assertEquals(1, methods.size());
    }


    @Test
    public void testFieldsThatHaveBoth() {
        final List<Field> fields = Annotator
                .of(new MarkedClass())
                .getFieldsThat(HaveAnnotation
                        .ofType(TestExtends.class)
                        .and(HaveAnnotation.ofType(TestExtendsWithValue.class)))
                .collect(Collectors.toList());

        Assertions.assertEquals(1, fields.size());
    }

    @Test
    public void testFieldsThatHaveOne() {
        final List<Field> fields = Annotator
                .of(new MarkedClass())
                .getFieldsThat(HaveAnnotation
                        .ofType(TestExtends.class)
                        .or(HaveAnnotation.ofType(TestExtendsWithValue.class)))
                .collect(Collectors.toList());
        Assertions.assertEquals(3, fields.size());
    }

    @Test
    public void testFieldsThatHaveOneAnnotationWithValue() {
        final List<Field> fields = Annotator
                .of(new MarkedClass())
                .getFieldsThat(HaveAnnotation
                        .ofType(TestExtends.class)
                        .or(HaveAnnotation
                                .ofType(TestExtendsWithValue.class)
                                .where(annot -> "myBothValue".equals(annot.value()))))
                .collect(Collectors.toList());
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    public void testFieldsThatHaveTwoAnnotationWithValue() {
        final List<Field> fields = Annotator
                .of(new MarkedClass())
                .getFieldsThat(HaveAnnotation
                        .ofType(TestExtendsWithValue.class)
                        .where(annot -> "myValue".equals(annot.value()))
                        .or(HaveAnnotation
                                .ofType(TestExtendsWithValue.class)
                                .where(annot -> "myBothValue".equals(annot.value()))))
                .collect(Collectors.toList());
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    public void testMultipleannotations(){
        Accessor<?> multipleAnnotation = Annotator.of(MarkedClass.class)
                                                  .getAccessor("multipleAnnotation");
        System.out.println(multipleAnnotation.getAnnotations());
    }
}