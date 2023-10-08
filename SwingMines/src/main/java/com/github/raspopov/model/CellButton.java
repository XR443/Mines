package com.github.raspopov.model;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CellButton extends JButton implements Cell {
    private final int x;
    private final int y;

    private boolean flagged;
    private boolean disabled;
    private boolean opened;
    private Optional<Field> field = Optional.empty();
    private CellInfo cellInfo;

    public CellButton(int x, int y) {
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
        this.flagged = flag;
        super.setEnabled(!flag);

        updateNeighbourBackground();
        backgroundColorCustom = flaggedColor;
    }

    private void updateNeighbourBackground() {
        field.ifPresent(f -> f.getAroundCells(this).stream()
                .map(c -> (CellButton) c)
                .peek(CellButton::updateBackground)
                .forEach(CellButton::repaint));
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    @Override
    public void open() {
        opened = true;
        flagged = false;
        setEnabled(false);

        updateNeighbourBackground();
    }

    @Override
    public boolean isOpen() {
        return opened;
    }

    @Override
    public Optional<CellInfo> getCellInfo() {
        return Optional.ofNullable(cellInfo);
    }

    private final Color backgroundColor = new Color(238, 238, 238);
    private final Color notAllMinesFind = new Color(255, 172, 172);
    private final Color allMinesFind = new Color(172, 255, 172);
    private final Color flaggedColor = new Color(140, 140, 240);

    private Color backgroundColorCustom;

    public void updateBackground() {
        if (isFlagged()) {
            backgroundColorCustom = flaggedColor;
            return;
        }

        if (cellInfo == null || !isOpen() || field.isEmpty()) {
            backgroundColorCustom = super.getBackground();
            return;
        }

        int minesAround = cellInfo.minesAround();
        List<CellButton> aroundCells = field.get().getAroundCells(this).stream()
                .map(c -> (CellButton) c)
                .toList();

        long flaggedAround = aroundCells.stream()
                .filter(CellButton::isFlagged)
                .count();

        long openedCellsCount = aroundCells.stream()
                .filter(CellButton::isOpen)
                .count();

        Color color;

        if (minesAround == flaggedAround && openedCellsCount == (aroundCells.size() - minesAround)) {
            color = backgroundColor;
        } else if (minesAround == flaggedAround) {
            color = allMinesFind;
        } else {
            color = notAllMinesFind;
        }
        backgroundColorCustom = color;
    }

    @Override
    public Color getBackground() {
        if (isFlagged())
            return flaggedColor;

        if (!isOpen() || backgroundColorCustom == null) {
            return super.getBackground();
        }
        return backgroundColorCustom;
    }

    public void setField(Field field) {
        this.field = Optional.ofNullable(field);
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
