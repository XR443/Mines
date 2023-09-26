package com.github.raspopov.ui;

import com.github.raspopov.service.FieldCreator;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class ButtonsPanelStatic extends ButtonsPanel {

    public ButtonsPanelStatic(MinesPanel minesPanel, FieldCreator fieldCreator) {
        super();
        JPanel innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);

        JButton button = new JButton("Create Field");
        button.addActionListener(e -> {
            minesPanel.createMinesField(fieldCreator.createField());
            updateUI();
        });
        innerPanel.add(button);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}
