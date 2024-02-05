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

package io.github.agache41.annotator.annotator;

import io.github.agache41.annotator.accessor.Accessor;
import io.github.agache41.annotator.matcher.HaveAnnotation;
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