package com.orm.reflection.accessor.base;

public interface Positionable {
    int getPosition();

    boolean hasPosition();

    int getLevel();

    boolean isAtRootLevel();

    Positionable getParent();
}
