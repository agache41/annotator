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

package io.github.agache41.annotator.accessor;

import java.util.Comparator;
import java.util.Objects;

/**
 * <pre>
 * The Comparator implementation for elements implementing the  Positionable interface.
 * </pre>
 */
public class PositionComparator implements Comparator<Positionable> {

    /**
     * <pre>
     * The constant SWITCH_PARENT_BEFORE_CHILDREN tells that in a multilevel hierarchy by ordering the parent comes before the children.
     * </pre>
     */
    public static final int SWITCH_PARENT_BEFORE_CHILDREN = 1; // or -1


    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Positionable pos1,
                       Positionable pos2) {
        int advance = 0;
        //go down until the same level
        while (pos1.getLevel() != pos2.getLevel()) {
            if (pos1.getLevel() > pos2.getLevel()) {
                pos1 = pos1.getParent();
                advance++;
            } else {
                pos2 = pos2.getParent();
                advance--;
            }
        }
        //if when drilling down we actually meet the parent
        if (pos1 == pos2 && advance != 0) {
            return SWITCH_PARENT_BEFORE_CHILDREN * advance;
        }
        //we continue to drill until we have the same parent
        while (!Objects.equals(pos1.getParent(), pos2.getParent())) {
            pos1 = pos1.getParent();
            pos2 = pos2.getParent();
        }
        final int compare = Integer.compare(pos1.getPosition(), pos2.getPosition());
        return compare;
    }
}
