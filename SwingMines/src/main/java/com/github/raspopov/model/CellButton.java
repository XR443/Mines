package com.github.raspopov.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class CellButton extends JButton {
    private final int x;
    private final int y;
    private final Map<Integer, CellButton> cellButtonMap;

    @Getter
    @Setter
    private Cell cell;

    private boolean flagged;
    private boolean opened;

//    public CellButton(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void setFlag(boolean flag) {
        this.flagged = flag;
        super.setEnabled(!flag);

        updateNeighbourBackground();
        backgroundColorCustom = flaggedColor;
    }

    private void updateNeighbourBackground() {
        Optional.ofNullable(cell)
                .ifPresent(c -> c.getAroundCells().stream()
                        .map(cell -> cellButtonMap.get(Objects.hash(cell.x(), cell.y())))
                        .peek(CellButton::updateBackground)
                        .forEach(CellButton::repaint));
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void open() {
        opened = true;
        flagged = false;
        setEnabled(false);

        updateNeighbourBackground();
    }

    public boolean isOpen() {
        return opened;
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

        if (cell == null || !isOpen()) {
            backgroundColorCustom = super.getBackground();
            return;
        }

        int minesAround = cell.getMinesAround();
        List<CellButton> aroundCells = cell.getAroundCells().stream()
                .map(c -> cellButtonMap.get(Objects.hash(c.x(), c.y())))
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
