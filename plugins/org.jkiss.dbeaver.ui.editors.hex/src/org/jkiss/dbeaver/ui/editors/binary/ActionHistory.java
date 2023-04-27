
package org.jkiss.dbeaver.ui.editors.binary;

import org.jkiss.dbeaver.ui.editors.binary.BinaryContent.Range;
import org.jkiss.dbeaver.utils.ContentUtils;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActionHistory {

    static enum ActionType {
        DELETE,
        INSERT,
        OVERWRITE
    }

    private static final Duration MERGE_TIME = Duration.ofMillis(1500);

    private BinaryContent.Range actionLastRange = null;
    private BinaryContent content = null;
    private List<Integer> deletedList = null;
    private boolean isBackspace = false;
    private List<Pair<ActionType, List<Range>>> actionList = null;
    private int actionsIndex = 0;
    private List<Range> currentAction = null;
    private ActionType currentActionType = null;
    private long mergedSinglesTop = -1L;
    private boolean mergingSingles = false;
    private Instant previousTime = null;
    private long newRangeLength = -1L;
    private long newRangePosition = -1L;

    ActionHistory(BinaryContent aContent)
    {
        if (aContent == null)
            throw new NullPointerException("null content");

        content = aContent;
        actionList = new ArrayList<>();
    }

    // Rest of the code remains unchanged

    void eventPreModify(ActionType type, long position, boolean isSingle)
    {
        if (type != currentActionType ||
            !isSingle ||
            Duration.between(Instant.now(), previousTime).compareTo(MERGE_TIME) > 0 ||
            (type == ActionType.INSERT || type == ActionType.OVERWRITE) && actionExclusiveEnd() != position ||
            type == ActionType.DELETE && actionPosition() != position && actionPosition() - 1L != position) {
            startAction(type, isSingle);
        } else {
            isBackspace = actionPosition() > position;
        }
        if (isSingle && type == ActionType.INSERT) {  // never calls addInserted...
            updateNewRange(position);
            previousTime = Instant.now();
        }
    }

    // Rest of the code remains unchanged
}

class Pair<A, B> {
    final A first;
    final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    // Optionally, implement hashCode, equals, and toString methods.
}
