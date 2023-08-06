package com.orm.reflection.annotator.accessor;

import com.orm.annotations.Position;
import com.orm.annotations.Recurse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Recurse
public class SubSubAnnotatedClass {
    @Position(3)
    private String f5;
    @Position(4)
    private String f6;
    @Position(1)
    private String f4;
}
