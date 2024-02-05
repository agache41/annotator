/*
 *    Copyright 2022-2023  Alexandru Agache
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.agache41.annotator.annotations;

import java.lang.annotation.*;

/**
 * <pre>
 * Marker annotation providing information about the extended annotation subtype.
 * This annotation is to be used on another annotation to mark its base class,
 * and it can also be used when selecting annotations.
 * A typical example would be :
 *
 *
 * Consider the following annotated annotation:
 *
 *   {@literal @}Extends(BaseTestClass.class)
 *   {@literal @}Retention(RetentionPolicy.RUNTIME)
 *      public  {@literal @}interface TestExtends {
 *   }
 *
 * And the class where it is used :
 *   {@literal @}TestExtends
 *   public class MarkedClass {
 *   ...
 *   }
 *
 * Then we can write the following code:
 *
 *  final List {@literal <Annotation>} annotations = Annotator
 *                 .of(new MarkedClass())
 *                 .getAnnotationsThat(HaveAnnotation
 *                                             .ofType(Extends.class)
 *                                             .having(AnExtendsValue.of(BaseTestClass.class)))
 *                 .collect(Collectors.toList());
 *
 *  Assertions.assertEquals(1, annotations.size());
 *  final Annotation annot = annotations.get(0);
 *  Assertions.assertNotNull(annot);
 *  Assertions.assertEquals(TestExtends.class, annot.annotationType());
 *
 * In this way we can select all class annotations that extend a given base class.
 * </pre>
 */
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Extends {
    /**
     * <pre>
     * The annotations that this annotation extends.
     * </pre>
     *
     * @return the base class(es)
     */
    Class<? extends Annotation>[] value();
}
