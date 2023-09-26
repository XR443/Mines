package com.github.raspopov.ui;

import com.github.raspopov.domain.Cell;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
public class CellButton extends JButton {
    private final Cell cell;
    @Setter
    private boolean flagged;
    private boolean disabled;

    public CellButton(Cell cell) {
        this.cell = cell;
    }

    // setDisabled используется если надо выключить внутренние листенеры кнопки
    public void setDisabled(boolean b) {
        super.setEnabled(!b);
        disabled = b;
    }

    @Override
    public Color getBackground() {
        if(isFlagged())
            return new Color(140, 140, 240);
        return super.getBackground();
    }
}
