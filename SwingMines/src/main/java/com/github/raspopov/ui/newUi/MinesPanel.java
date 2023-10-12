package com.github.raspopov.ui.newUi;

import com.github.raspopov.model.CellButton;
import com.github.raspopov.model.StaticField;
import com.github.raspopov.service.FieldCreator;
import com.github.raspopov.utils.FlaggedMinesCount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

@Component
@Primary
public class MinesPanel extends JPanel {
    private final JPanel innerPanel;
    private final FieldCreator fieldCreator;
    private final FlaggedMinesCount flaggedMinesCount;
    private final ApplicationEventPublisher applicationEventPublisher;
    private StaticField field;

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

    private void clear() {
        innerPanel.removeAll();
        this.field = null;
        flaggedMinesCount.clear();
        updateUI();
    }

    public void createMinesField(int width, int height) {
        clear();

        field = fieldCreator.createField(width, height);

        HashMap<Integer, CellButton> cellButtonMap = new HashMap<>();
        ButtonActionListener buttonActionListener = new ButtonActionListener(flaggedMinesCount,
                field,
                applicationEventPublisher,
                cellButtonMap,
                () -> {
                    clear();
                    createMinesField(width, height);
                    updateUI();
                },
                this::clear);

        innerPanel.setLayout(new GridLayout(height, width));

        Dimension preferredSize = new Dimension(45, 45);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellButton button = new CellButton(x, y, cellButtonMap);

                button.addActionListener(buttonActionListener);
                button.addMouseListener(buttonActionListener);

                button.setUI(new CellButtonUI(button));

                button.setPreferredSize(preferredSize);
                innerPanel.add(button);

                cellButtonMap.put(Objects.hash(x, y), button);
            }
        }
    }
}
