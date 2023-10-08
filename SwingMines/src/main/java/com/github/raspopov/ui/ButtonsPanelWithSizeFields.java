package com.github.raspopov.ui;

import com.github.raspopov.ui.newUi.MinesPanel;
import com.github.raspopov.utils.MinesCountEvent;
import com.github.raspopov.utils.WinLoseEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

@Component
@Primary
public class ButtonsPanelWithSizeFields extends ButtonsPanel {

    private final JLabel minesCountLabel;
    private final JLabel flaggedMinesCountLabel;
    private final JLabel resultLabel;

    public ButtonsPanelWithSizeFields(MinesPanel minesPanel) {
        super();
        JPanel innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);

        resultLabel = new JLabel();
        innerPanel.add(resultLabel);

        JButton button = new JButton("Create Field");

        innerPanel.add(button);

        JLabel widthLabel = new JLabel("Width");
        innerPanel.add(widthLabel);

        JFormattedTextField width = new JFormattedTextField(getNumberFormat());
        width.setValue(40L);
        width.setColumns(5);

        innerPanel.add(width);

        JLabel heightLabel = new JLabel("Height");
        innerPanel.add(heightLabel);

        JFormattedTextField height = new JFormattedTextField(getNumberFormat());
        height.setValue(20L);
        height.setColumns(5);

        innerPanel.add(height);

        button.addActionListener(e -> {
            resultLabel.setText("");

            long widthVal = (long) width.getValue();
            long heightVal = (long) height.getValue();
            minesPanel.createMinesField((int) widthVal, (int) heightVal);
            updateUI();
        });


        this.flaggedMinesCountLabel = new JLabel("0");
        innerPanel.add(flaggedMinesCountLabel);

        JLabel split = new JLabel("/");
        innerPanel.add(split);

        this.minesCountLabel = new JLabel("0");
        innerPanel.add(minesCountLabel);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private static NumberFormat getNumberFormat() {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMinimumIntegerDigits(1);
        numberInstance.setMaximumIntegerDigits(3);
        return numberInstance;
    }

    @EventListener
    public void updateMinesCount(MinesCountEvent minesCountEvent) {
        flaggedMinesCountLabel.setText(String.valueOf(minesCountEvent.flaggedMinesCount()));
        minesCountLabel.setText(String.valueOf(minesCountEvent.minesCount()));
        updateUI();
    }

    @EventListener
    public void winLoseLabel(WinLoseEvent winLoseEvent) {
        resultLabel.setText(winLoseEvent.message());
        updateUI();
    }
}
