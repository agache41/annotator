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

/**
 * <pre>
 * The interface Positionable defines the logic for ordering elements like fields on a composed ordered structure of levels.
 * </pre>
 */
public interface Positionable {
    /**
     * <pre>
     * Gets position inside the level.
     * </pre>
     *
     * @return the element position
     */
    int getPosition();

    /**
     * <pre>
     * Tells if this element has a position.
     * </pre>
     *
     * @return true if this element has a position, false otherwise
     */
    boolean hasPosition();

    /**
     * <pre>
     * The current level on which the element sits.
     * </pre>
     *
     * @return the level of the element
     */
    int getLevel();

    /**
     * <pre>
     * Tells if the element sits on the root level.
     * </pre>
     *
     * @return true if this element sits on the root level, false otherwise
     */
    boolean isAtRootLevel();

    /**
     * <pre>
     *  Returns the parent of this element, if any
     * </pre>
     *
     * @return the parent element.
     */
    Positionable getParent();
}
