package com.github.raspopov.ui.newUi;

import com.github.raspopov.model.Cell;
import com.github.raspopov.model.CellButton;
import com.github.raspopov.model.StaticField;
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
import java.util.Map;
import java.util.Objects;

@Log4j2
public class ButtonActionListener extends MouseAdapter implements ActionListener {

    private static final String WIN_MESSAGE = "You win!\nWant again?";
    private static final String LOSE_MESSAGE = "You lose :(\nWant again?";

    private final StaticField field;
    private final Map<Integer, CellButton> cellButtonMap;
    private final FlaggedMinesCount flaggedMinesCount;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final Runnable againCallback;

    private final Runnable cancelCallback;


    public ButtonActionListener(FlaggedMinesCount flaggedMinesCount,
                                StaticField field,
                                ApplicationEventPublisher applicationEventPublisher,
                                Map<Integer, CellButton> cellButtonMap,
                                Runnable againCallback,
                                Runnable cancelCallback) {
        this.flaggedMinesCount = flaggedMinesCount;
        this.field = field;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cellButtonMap = cellButtonMap;
        this.againCallback = againCallback;
        this.cancelCallback = cancelCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (field.isGameEnded())
            return;

        CellButton cellButton = (CellButton) e.getSource();

        log.info(cellButton + " Pressed");

        Cell cell = field.cellOf(cellButton.x(), cellButton.y());
        pressCell(cell);
    }

    private void pressCell(Cell pressedCell) {
        List<Cell> openedCells = pressedCell.open();
//        generatedCells.forEach(cell -> ((CellButton) cell).setField(field));

        processMove(pressedCell, openedCells);
    }

    private void processMove(Cell pressedCell, List<Cell> cells) {
        processCells(pressedCell, cells);

        processGameResult();
    }

    private void processGameResult() {
        if (field.isGameEnded()) {
//            showPopup(field.getWin() ? WIN_MESSAGE : LOSE_MESSAGE);

            sendWinEvent(field.isGameIsWon(), field.isGameIsWon() ? WIN_MESSAGE : LOSE_MESSAGE);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (field.isGameEnded())
            return;

        CellButton cellButton = (CellButton) e.getSource();

        Cell cell = field.cellOf(cellButton.x(), cellButton.y());
        cellButton.setCell(cell);

        if (cellButton.isOpen()) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                List<Cell> aroundCells = cell.getAroundCells();
                if (cell.getMinesAround() == aroundCells.stream()
                        .map(c -> cellButtonMap.get(Objects.hash(c.x(), c.y())))
                        .filter(CellButton::isFlagged)
                        .count())
                    for (Cell aroundCell : aroundCells.stream()
                            .filter(c -> !cellButtonMap.get(Objects.hash(c.x(), c.y())).isFlagged())
                            .toList()) {
                        if (field.isGameInProgress() && !aroundCell.isOpen()) {
                            pressCell(aroundCell);
                        }
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

    private void processCells(Cell pressedCell, List<Cell> cells) {
        if (pressedCell.isMine()) {
            cells.stream()
                    .map(cell -> cellButtonMap.get(Objects.hash(cell.x(), cell.y())))
                    .forEach(cellButton -> {
                        cellButton.setText("*");
                        cellButton.setForeground(Color.RED);
                    });
            return;
        }

        cells.stream()
                .map(cell -> {
                    CellButton cellButton = cellButtonMap.get(Objects.hash(cell.x(), cell.y()));
                    cellButton.setCell(cell);
                    return cellButton;
                })
                .forEach(cellButton -> {
                    cellButton.setText(String.valueOf(cellButton.getCell().getMinesAround()));
                    cellButton.open();
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
