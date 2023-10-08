package com.github.raspopov.ui.newUi;

import com.github.raspopov.model.CellButton;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;

@RequiredArgsConstructor
public class CellButtonUI extends MetalButtonUI {
    private final CellButton cellButton;

    @Override
    protected Color getDisabledTextColor() {
        return cellButton.getForeground();
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        super.paintText(g, c, textRect, text);
    }
}
