package com.orm.reflection.annotator.accessor;

import com.orm.annotations.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubAnnotatedClass {
    @Position(3)
    private String f7;
    @Position(4)
    private String f8;
    @Position(2)
    private SubSubAnnotatedClass r2;
    @Position(1)
    private String f3;
}
