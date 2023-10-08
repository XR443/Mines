package com.github.raspopov.ui.newUi;

import com.github.raspopov.model.*;
import com.github.raspopov.service.FieldCreator;
import com.github.raspopov.utils.FlaggedMinesCount;
import com.github.raspopov.utils.WinLoseEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
public class ButtonActionListener extends MouseAdapter implements ActionListener {

    private static final String WIN_MESSAGE = "You win!\nWant again?";
    private static final String LOSE_MESSAGE = "You lose :(\nWant again?";

    private Field field;
    private final int width;
    private final int height;
    private final FieldCreator fieldCreator;
    private final Set<Cell> generatedCells;
    private final FlaggedMinesCount flaggedMinesCount;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final Runnable againCallback;

    private final Runnable cancelCallback;


    public ButtonActionListener(FlaggedMinesCount flaggedMinesCount,
                                int width, int height,
                                Set<Cell> generatedCells,
                                FieldCreator fieldCreator,
                                ApplicationEventPublisher applicationEventPublisher,
                                Runnable againCallback,
                                Runnable cancelCallback) {
        this.flaggedMinesCount = flaggedMinesCount;
        this.width = width;
        this.height = height;
        this.fieldCreator = fieldCreator;
        this.generatedCells = generatedCells;
        this.applicationEventPublisher = applicationEventPublisher;
        this.againCallback = againCallback;
        this.cancelCallback = cancelCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CellButton cellButton = (CellButton) e.getSource();

        if (field == null)
            field = fieldCreator.createField(width, height, generatedCells, cellButton);

        generatedCells.forEach(cell -> ((CellButton) cell).setField(field));

        log.info(cellButton + " Pressed");

        processMove(cellButton);
    }

    private void processMove(Cell cell) {
        Result result = field.processMove(cell);

        processCellInfoList(result.cells());

        processGameResult(result);
    }

    private void processGameResult(Result result) {
        if (field.isGameInProgress() && field.getWin() == null) {
            if (!result.success()) {
//                showPopup(LOSE_MESSAGE);
                sendWinEvent(false, LOSE_MESSAGE);
            }
        } else if (field.getWin() != null) {
//            showPopup(field.getWin() ? WIN_MESSAGE : LOSE_MESSAGE);
            sendWinEvent(field.getWin(), field.getWin() ? WIN_MESSAGE : LOSE_MESSAGE);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        CellButton cellButton = (CellButton) e.getSource();

        if (cellButton.isOpen()) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                CellInfo cellInfo = cellButton.getCellInfo().get();
                List<Cell> aroundCells = field.getAroundCells(cellButton);
                if (cellInfo.minesAround() == aroundCells.stream()
                        .filter(Cell::isFlagged)
                        .count())
                    for (Cell aroundCell : aroundCells.stream()
                            .filter(cell -> !cell.isFlagged())
                            .toList()) {
                        if (field.isGameInProgress())
                            processMove(aroundCell);
                    }
            }
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            if (!cellButton.isOpen()) {
                cellButton.setFlag(!cellButton.isFlagged());
                if (cellButton.isFlagged()) {
                    cellButton.setText("F");
                    flaggedMinesCount.increment();
                } else {
                    cellButton.setText(null);
                    flaggedMinesCount.decrement();
                }
            }
        }
    }

    private void processCellInfoList(List<Cell> cells) {
        if (cells.stream()
                .map(Cell::getCellInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CellInfo::cellType)
                .anyMatch(CellType.MINE::equals)) {
            cells.stream()
                    .map(cell -> (CellButton) field.getCells().get(cell))
                    .forEach(cellButton -> {
                        cellButton.setText("*");
                        cellButton.setForeground(Color.RED);
                    });
            return;
        }

        cells.stream()
                .map(cell -> (CellButton) field.getCells().get(cell))
                .filter(cellButton -> cellButton.getCellInfo().isPresent())
                .forEach(cellButton -> {
                    cellButton.setText(String.valueOf(cellButton.getCellInfo().get().minesAround()));
                    cellButton.updateBackground();
                    cellButton.repaint();
                });
    }

    private void showPopup(String message) {
        int confirmDialog = JOptionPane.showConfirmDialog(null,
                message,
                "Again?",
                JOptionPane.YES_NO_OPTION);
        if (confirmDialog == 0) {
            againCallback.run();
        } else if (confirmDialog == 1 || confirmDialog == -1) {
            cancelCallback.run();
        }
    }

    private void sendWinEvent(boolean win, String message) {
        applicationEventPublisher.publishEvent(new WinLoseEvent(win, message.replace("\n", " ")));
    }
}
