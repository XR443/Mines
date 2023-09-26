package com.github.raspopov.ui;

import com.github.raspopov.domain.Cell;
import com.github.raspopov.domain.Field;
import com.github.raspopov.service.FieldCreator;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class MinesPanel extends JPanel {

    private final JPanel innerPanel;
    private final FieldCreator fieldCreator;
    private Field field;

    private Map<Cell, CellButton> cellToButton;

    public MinesPanel(FieldCreator fieldCreator) {
        super();
        innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.fieldCreator = fieldCreator;
    }

    public void createMinesField(Field field) {
        this.field = field;
        createMinesField(field.getWidth(), field.getHeight());
    }

    private void clear() {
        innerPanel.removeAll();
        if (cellToButton != null)
            cellToButton.clear();
        cellToButton = null;
        this.field = null;
        updateUI();
    }

    public void createMinesField(int width, int height) {
        clear();

        cellToButton = new HashMap<>(height * width, 1);

        ButtonActionListener buttonActionListener;
        if (field == null) {
            buttonActionListener = new ButtonActionListener(width, height, fieldCreator,
                    cellToButton,
                    () -> {
                        clear();
                        createMinesField(width, height);
                        updateUI();
                    },
                    this::clear);
        } else {
            buttonActionListener = new ButtonActionListener(field,
                    cellToButton,
                    () -> {
                        clear();
                        createMinesField(field);
                        updateUI();
                    },
                    this::clear);
        }

        innerPanel.setLayout(new GridLayout(height, width));

        Dimension preferredSize = new Dimension(45, 45);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Cell cell = new Cell(i, j);

                CellButton button = new CellButton(cell);

                button.addActionListener(buttonActionListener);
                button.addMouseListener(buttonActionListener);

                button.setUI(new CellButtonUI(button));

                button.setPreferredSize(preferredSize);
                innerPanel.add(button);

                cellToButton.put(button.getCell(), button);
            }
        }
    }
}
