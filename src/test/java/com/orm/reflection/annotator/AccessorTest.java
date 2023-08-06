package com.orm.reflection.annotator;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.matcher.HaveAnnotation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessorTest {
    @Test
    public void test() {
        final List<Accessor<?>> accessors = Annotator
                .of(new MarkedClass())
                .getAccessorsThat(HaveAnnotation.ofType(TestExtends.class))
                .collect(Collectors.toList());
        System.out.println(Annotator.of(new MarkedClass())
                                    .toString());
        assertEquals(3, accessors.size());
    }

}