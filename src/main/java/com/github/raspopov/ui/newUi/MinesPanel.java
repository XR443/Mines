package com.github.raspopov.ui.newUi;

import com.github.raspopov.model.Cell;
import com.github.raspopov.model.CellButton;
import com.github.raspopov.model.Field;
import com.github.raspopov.service.FieldCreator;
import com.github.raspopov.utils.FlaggedMinesCount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

@Component
@Primary
public class MinesPanel extends JPanel {
    private final JPanel innerPanel;
    private final FieldCreator fieldCreator;
    private final FlaggedMinesCount flaggedMinesCount;
    private final ApplicationEventPublisher applicationEventPublisher;
    private Field field;

    public MinesPanel(FieldCreator fieldCreator,
                      FlaggedMinesCount flaggedMinesCount,
                      ApplicationEventPublisher applicationEventPublisher) {
        super();

        this.fieldCreator = fieldCreator;
        this.flaggedMinesCount = flaggedMinesCount;
        this.applicationEventPublisher = applicationEventPublisher;

        innerPanel = new JPanel();
        add(innerPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void createMinesField(Field field) {
        this.field = field;
        createMinesField(field.getWidth(), field.getHeight());
    }

    private void clear() {
        innerPanel.removeAll();
        this.field = null;
        flaggedMinesCount.clear();
        updateUI();
    }

    public void createMinesField(int width, int height) {
        clear();

        Set<Cell> cells = new HashSet<>();
        ButtonActionListener buttonActionListener = new ButtonActionListener(flaggedMinesCount,
                width,
                height,
                cells,
                fieldCreator,
                applicationEventPublisher,
                () -> {
                    clear();
                    createMinesField(width, height);
                    updateUI();
                },
                this::clear);

        innerPanel.setLayout(new GridLayout(height, width));

        Dimension preferredSize = new Dimension(45, 45);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                CellButton button = new CellButton(i, j);

                button.addActionListener(buttonActionListener);
                button.addMouseListener(buttonActionListener);

                button.setUI(new CellButtonUI(button));

                button.setPreferredSize(preferredSize);
                innerPanel.add(button);
                cells.add(button);
            }
        }
    }
}
