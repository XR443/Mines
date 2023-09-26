package com.github.raspopov.ui;

import com.github.raspopov.service.FieldCreator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

@Component
@Primary
public class ButtonsPanelWithSizeFields extends ButtonsPanel {

    public ButtonsPanelWithSizeFields(MinesPanel minesPanel) {
        super();
        JPanel innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);

        JButton button = new JButton("Create Field");

        innerPanel.add(button);

        JLabel widthLabel = new JLabel("Width");
        innerPanel.add(widthLabel);

        JFormattedTextField width = new JFormattedTextField(getNumberFormat());
        width.setValue(20L);
        width.setColumns(5);

        innerPanel.add(width);

        JLabel heightLabel = new JLabel("Height");
        innerPanel.add(heightLabel);

        JFormattedTextField height = new JFormattedTextField(getNumberFormat());
        height.setValue(20L);
        height.setColumns(5);

        innerPanel.add(height);

        button.addActionListener(e -> {
            long widthVal = (long) width.getValue();
            long heightVal = (long) height.getValue();
            minesPanel.createMinesField((int) widthVal, (int) heightVal);
            updateUI();
        });

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private static NumberFormat getNumberFormat() {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMinimumIntegerDigits(1);
        numberInstance.setMaximumIntegerDigits(3);
        return numberInstance;
    }
}
