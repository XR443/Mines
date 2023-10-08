package com.github.raspopov.model;

import java.util.Objects;
import java.util.Optional;

public class MineCell implements Cell {
    private final int x;
    private final int y;
    private CellInfo cellInfo;

    public MineCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public void setFlag(boolean flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFlagged() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void open() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<CellInfo> getCellInfo() {
        return Optional.of(cellInfo);
    }

    @Override
    public void setCellInfo(CellInfo cellInfo) {
        this.cellInfo = cellInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Cell that))
            return false;
        return this.x() == that.x() &&
                this.y() == that.y();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Cell[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }
}
