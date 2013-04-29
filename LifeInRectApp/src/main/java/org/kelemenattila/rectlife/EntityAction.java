package org.kelemenattila.rectlife;

/**
 *
 * @author Kelemen Attila
 */
 public enum EntityAction {
    TOP_LEFT(AttackPosition.TOP_LEFT),
    TOP(AttackPosition.TOP),
    TOP_RIGHT(AttackPosition.TOP_RIGHT),
    LEFT(AttackPosition.LEFT),
    RIGHT(AttackPosition.RIGHT),
    BOTTOM_LEFT(AttackPosition.BOTTOM_LEFT),
    BOTTOM(AttackPosition.BOTTOM),
    BOTTOM_RIGHT(AttackPosition.BOTTOM_RIGHT),
    DO_NOTHING(null);

    private final AttackPosition action;

    private EntityAction(AttackPosition action) {
        this.action = action;
    }

    public AttackPosition getAction() {
        return action;
    }

    public enum AttackPosition {
        TOP_LEFT(-1, -1),
        TOP(0, -1),
        TOP_RIGHT(1, -1),
        LEFT(-1, 0),
        RIGHT(1, 0),
        BOTTOM_LEFT(-1, 1),
        BOTTOM(0, 1),
        BOTTOM_RIGHT(1, 1),
        SELF(0, 0);

        private final int dx;
        private final int dy;

        private AttackPosition(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }
    }
}
