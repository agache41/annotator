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
 * The annotation Recurse tells if a specific field has to be processed in the the current Annotator instance.
 * A typical Example would be
 *
 * public class AnnotatedClass {
 *     {@literal @}Position(1)
 *     private String name;
 *     {@literal @}Recurse
 *     {@literal @}Position(2)
 *     private SubAnnotatedClass s1;
 *     {@literal @} Position(3)
 *     private String f1;
 *     }
 * This will make the Annotator to recurse in the class SubAnnotatedClass and process its fields.
 * The main use is when modelling with composed subclasses.
 * </pre>
 */
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Recurse {
}
