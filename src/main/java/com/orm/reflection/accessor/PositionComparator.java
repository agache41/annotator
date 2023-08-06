package com.orm.reflection.accessor;

import com.orm.reflection.accessor.base.Positionable;

import java.util.Comparator;
import java.util.Objects;

public class PositionComparator implements Comparator<Positionable> {

    public static final int SWITCH_PARENT_BEFORE_CHILDREN = 1; // or -1


    @Override
    public int compare(Positionable pos1, Positionable pos2) {
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
