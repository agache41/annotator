package com.orm.reflection.annotator.accessor;

import com.orm.annotations.Position;
import com.orm.annotations.Recurse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnnotatedClass {
    @Position(5)
    private SubAnotatedPlaceholder r3;
    @Position(2)
    private String f2;
    @Position(4)
    private String f9;
    @Recurse
    @Position(3)
    private SubAnnotatedClass r1;
    @Position(1)
    private String f1;
}
