package com.orm.reflection.annotator.accessor;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.accessor.PositionComparator;
import com.orm.reflection.annotator.Annotator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccessorTest {
    private List<Accessor<?>> accessorList;
    private List<Accessor<?>> accessorLeafList;

    private AnnotatedClass root;

    @BeforeEach
    void setUp() {
        this.accessorList = Annotator
                .of(AnnotatedClass.class)
                .getAccessors()
                .sorted(/*new PositionComparator()*/)
                .collect(Collectors.toList());
        assertEquals(22, this.accessorList.size());

        this.accessorLeafList = Annotator
                .of(AnnotatedClass.class)
                .getAccessors()
                .sorted(new PositionComparator())
                .filter(Accessor::isLeaf)
                .collect(Collectors.toList());
        assertEquals(17, this.accessorLeafList.size());

        this.root = new AnnotatedClass();
        this.accessorLeafList.stream()
                             .forEach(acc -> acc.set(this.root, acc.getField()
                                                              .getName()));
        assertEquals("AnnotatedClass(r3=SubAnotatedPlaceholder(f10=f10," +
                " r4=SubAnnotatedClass(f7=f7, f8=f8," +
                " r2=SubSubAnnotatedClass(f5=f5, f6=f6, f4=f4), f3=f3), f11=f11)," +
                " f2=f2, f9=f9," +
                " r1=SubAnnotatedClass(f7=f7, f8=f8," +
                " r2=SubSubAnnotatedClass(f5=f5, f6=f6, f4=f4), f3=f3)," +
                " f1=f1)", this.root.toString());

    }

    @Test
    void testLeafAccessorsOrder() {
        assertEquals("f1,f2,r1.f3,r1.r2.f4,r1.r2.f5,r1.r2.f6,r1.f7,r1.f8,f9,r3.f10," +
                "r3.r4.f3,r3.r4.r2.f4,r3.r4.r2.f5,r3.r4.r2.f6,r3.r4.f7,r3.r4.f8,r3.f11", this.accessorLeafList
                .stream()
                .map(Accessor::getName)
                .collect(Collectors.joining(",")));
    }

    @Test
    void testAllAccessorsOrder() {
        assertEquals("f1,f2,r1,r1.f3,r1.r2,r1.r2.f4,r1.r2.f5,r1.r2.f6,r1.f7,r1.f8,f9,r3,r3.f10," +
                "r3.r4,r3.r4.f3,r3.r4.r2,r3.r4.r2.f4,r3.r4.r2.f5,r3.r4.r2.f6,r3.r4.f7,r3.r4.f8,r3.f11", this.accessorList
                .stream()
                .map(Accessor::getName)
                .collect(Collectors.joining(",")));
    }

    @Test
    void testNPEonRead() {
        AnnotatedClass root = new AnnotatedClass();
        root.setF1("testField1");
        root.setF2("testField2");
        root.setR1(null);
        root.setF9("testField4");

        assertThrows(NullPointerException.class, () -> this.accessorLeafList
                .stream()
                .map(acc -> acc.get(root))
                .map(Object::toString)
                .collect(Collectors.joining(",")));
    }

    @Test
    void testRead() {
        AnnotatedClass root = new AnnotatedClass();
        root.setF1("f1");
        root.setF2("f2");
        root.setR1(new SubAnnotatedClass());
        root.getR1()
            .setF3("f3");
        root.getR1()
            .setR2(new SubSubAnnotatedClass());
        root.getR1()
            .getR2()
            .setF4("f4");
        root.getR1()
            .getR2()
            .setF5("f5");
        root.getR1()
            .getR2()
            .setF6("f6");
        root.getR1()
            .setF7("f7");
        root.getR1()
            .setF8("f8");
        root.setF9("f9");
        root.setR3(new SubAnotatedPlaceholder());
        root.getR3()
            .setF10("f10");
        root.getR3()
            .setF11("f11");
        root.getR3()
            .setR4(root.getR1());

        assertEquals("f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f3,f4,f5,f6,f7,f8,f11", this.accessorLeafList
                .stream()
                .map(acc -> acc.get(root))
                .map(Object::toString)
                .collect(Collectors.joining(",")));
    }

    @Test
    void testAutomaticRead() {
        assertEquals("f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f3,f4,f5,f6,f7,f8,f11", this.accessorLeafList
                .stream()
                .map(acc -> acc.get(this.root))
                .map(Object::toString)
                .collect(Collectors.joining(",")));
    }
}