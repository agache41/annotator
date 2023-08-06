package com.orm.reflection.annotator.accessor;

import com.orm.annotations.Position;
import com.orm.annotations.Recurse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Recurse
public class SubAnotatedPlaceholder {

    @Position(0)
    private String f10;

    @Recurse
    @Position(1)
    private SubAnnotatedClass r4;

    @Position(2)
    private String f11;


}
