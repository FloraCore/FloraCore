package team.floracore.bukkit.inevntory.content;

public class SlotPos {
    private final int row;
    private final int column;

    public SlotPos(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static SlotPos of(int row, int column) {
        return new SlotPos(row, column);
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SlotPos slotPos = (SlotPos) obj;
        return row == slotPos.row && column == slotPos.column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
