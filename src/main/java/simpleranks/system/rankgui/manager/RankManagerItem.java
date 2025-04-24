package simpleranks.system.rankgui.manager;

public enum RankManagerItem {
    EDIT_NAME(10), EDIT_COLOR(11), EDIT_GROUP(12), MOVE_UP(15), MOVE_DOWN(16),

    CREATE_TYPE(10), CREATE_NAME(11), CREATE_COLOR(12), CREATE_GROUP(13), CREATE_CANCEL(15), CREATE_CONFIRM(16),

    CREATE(25), BACK(26),
    NEXT_PAGE(19), PREVIOUS_PAGE(18);

    private int slot;
    RankManagerItem(int slot) {
        this.slot = slot;
    }

    public int slot() {
        return this.slot;
    }
}
