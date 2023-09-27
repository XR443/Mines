package com.github.raspopov.ui;

import com.github.raspopov.domain.Cell;
import com.github.raspopov.domain.Field;
import com.github.raspopov.service.FieldCreator;
import com.github.raspopov.utils.FlaggedMinesCount;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class MinesPanel extends JPanel {

    private final JPanel innerPanel;
    private final FieldCreator fieldCreator;
    private final FlaggedMinesCount flaggedMinesCount;
    private Field field;

    private Map<Cell, CellButton> cellToButton;

    public MinesPanel(FieldCreator fieldCreator, FlaggedMinesCount flaggedMinesCount) {
        super();
        innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.fieldCreator = fieldCreator;
        this.flaggedMinesCount = flaggedMinesCount;
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
        flaggedMinesCount.clear();
        updateUI();
    }

    public void createMinesField(int width, int height) {
        clear();

        cellToButton = new HashMap<>(height * width, 1);

        ButtonActionListener buttonActionListener;
        if (field == null) {
            buttonActionListener = new ButtonActionListener(flaggedMinesCount,
                    width, height, fieldCreator,
                    cellToButton,
                    () -> {
                        clear();
                        createMinesField(width, height);
                        updateUI();
                    },
                    this::clear);
        } else {
            buttonActionListener = new ButtonActionListener(flaggedMinesCount,
                    field,
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
