package com.github.raspopov.model;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class Cell {

    private final int x;
    private final int y;

    private final StaticField field;
    @Getter
    private List<Cell> aroundCells;
    @Getter
    private final CellType type;
    @Getter
    private boolean open;
    private boolean cellInitialized;
    @Getter
    private int minesAround;

    protected Cell(int x,
                   int y,
                   CellType type,
                   StaticField field) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.field = field;
    }

    protected void initializeAroundCells() {
        if (cellInitialized)
            return;

        this.aroundCells = field.getAroundCells(this);

        int minesAround = 0;
        for (Cell aroundCell : this.aroundCells) {
            if (aroundCell.isMine())
                minesAround += 1;
        }
        this.minesAround = minesAround;

        cellInitialized = true;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public List<Cell> open() {
        initializeAroundCells();
        if (isOpen())
            throw new IllegalStateException("Cell is already open. Check cell state using isOpen method");
        open = true;
        return field.openCell(this);
    }

    public boolean isMine() {
        return getType().equals(CellType.MINE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
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
    public String toString() {
        return "Cell[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }
}
