package com.orm.temporary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassAnnotatorTest {
    @Test
    public void nameTest() {
        ClassAnnotator<TestBean> classAnnotator = ClassAnnotator.ofClass(TestBean.class);
        assertEquals("TestBean", classAnnotator.getName());
        List<FieldAnnotator> fields = classAnnotator.getFieldDescriptorsWith(TestAnnotation.class);
        assertEquals(1, fields.size());
        FieldAnnotator<String> fieldAnnotator = fields.get(0);
        assertEquals("nameWithAnnotation", fieldAnnotator.getName());
    }
}