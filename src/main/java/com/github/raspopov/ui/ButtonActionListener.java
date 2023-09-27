package com.github.raspopov.ui;

import com.github.raspopov.domain.*;
import com.github.raspopov.service.FieldCreator;
import com.github.raspopov.utils.FlaggedMinesCount;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

@Log4j2
public class ButtonActionListener extends MouseAdapter implements ActionListener {

    private static final String WIN_MESSAGE = "You win!\nWant again?";
    private static final String LOSE_MESSAGE = "You lose :(\nWant again?";

    private Field field;
    private int width;
    private int height;
    private FieldCreator fieldCreator;
    private final FlaggedMinesCount flaggedMinesCount;
    private final Map<Cell, CellButton> cellToButton;

    private final Runnable againCallback;

    private final Runnable cancelCallback;

    private final Color backgroundColor = new Color(238, 238, 238);
    private final Color notAllMinesFind = new Color(255, 172, 172);
    private final Color allMinesFind = new Color(172, 255, 172);

    public ButtonActionListener(FlaggedMinesCount flaggedMinesCount,
                                Field field,
                                Map<Cell, CellButton> cellToButton,
                                Runnable againCallback,
                                Runnable cancelCallback) {
        this.flaggedMinesCount = flaggedMinesCount;
        this.field = field;
        this.cellToButton = cellToButton;
        this.againCallback = againCallback;
        this.cancelCallback = cancelCallback;
    }

    public ButtonActionListener(FlaggedMinesCount flaggedMinesCount,
                                int width, int height, FieldCreator fieldCreator,
                                Map<Cell, CellButton> cellToButton,
                                Runnable againCallback,
                                Runnable cancelCallback) {
        this.flaggedMinesCount = flaggedMinesCount;
        this.width = width;
        this.height = height;
        this.fieldCreator = fieldCreator;
        this.cellToButton = cellToButton;
        this.againCallback = againCallback;
        this.cancelCallback = cancelCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CellButton cellButton = (CellButton) e.getSource();

        if (field == null)
            field = fieldCreator.createField(width, height, cellButton.getCell());

        cellButton.setDisabled(false);

        Cell cell = cellButton.getCell();

        log.info(cell + " Pressed");

        processMove(cell);
    }

    private void processMove(Cell cell) {
        Result result = field.processMove(cell);

        processCellInfoList(result.cellInfoList());

        markCells();

        processGameResult(result);
    }

    private void markCells() {
        field.getOpenedCells().stream()
                .map(cellToButton::get)
                .forEach(cb -> {
                    int minesAround = field.getCellInfo(cb.getCell()).minesAround();
                    List<Cell> aroundCells = field.getAroundCells(cb.getCell());
                    long flaggedAround = aroundCells.stream()
                            .filter(cell -> cellToButton.get(cell).isFlagged())
                            .count();

                    long openedCellsCount = aroundCells.stream()
                            .filter(cell -> field.getOpenedCells().contains(cell))
                            .count();
                    if (field.getOpenedCells().containsAll(aroundCells)) {
                        cb.setBackground(backgroundColor);
                    } else if (minesAround == flaggedAround && openedCellsCount == (aroundCells.size() - minesAround)) {
                        cb.setBackground(backgroundColor);
                    } else if (minesAround == flaggedAround) {
                        cb.setBackground(allMinesFind);
                    } else {
                        cb.setBackground(notAllMinesFind);
                    }
                });
    }

    private void processGameResult(Result result) {
        if (field.isGameInProgress() && field.getWin() == null) {
            if (!result.success()) {
                showPopup(LOSE_MESSAGE);
            }
        } else if (field.getWin() != null) {
            showPopup(field.getWin() ? WIN_MESSAGE : LOSE_MESSAGE);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        CellButton cellButton = (CellButton) e.getSource();

        if (cellButton.isDisabled()) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                CellInfo cellInfo = field.getCellInfo(cellButton.getCell());
                List<Cell> aroundCells = field.getAroundCells(cellButton.getCell());
                if (cellInfo.minesAround() == aroundCells.stream()
                        .filter(cell -> cellToButton.get(cell).isFlagged())
                        .count())
                    for (Cell aroundCell : aroundCells.stream()
                            .filter(cell -> !cellToButton.get(cell).isFlagged())
                            .toList()) {
                        if (field.isGameInProgress())
                            processMove(aroundCell);
                    }
            }
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            if (!cellButton.isDisabled()) {
                cellButton.setFlagged(!cellButton.isFlagged());
                if (cellButton.isFlagged()) {
                    cellButton.setText("F");
//                cellButton.setForeground(Color.BLUE);
//                cellButton.setBackground(flagColor);
                    cellButton.setEnabled(false);

                    flaggedMinesCount.increment();
                } else {
                    cellButton.setText(null);
//                cellButton.setForeground(Color.BLACK);
//                cellButton.setBackground(backgroundColor);
                    cellButton.setEnabled(true);

                    flaggedMinesCount.decrement();
                }
            }
        }

        markCells();
    }

    private void processCellInfoList(List<CellInfo> cellInfoList) {
        for (CellInfo cellInfo : cellInfoList) {
            CellButton button = cellToButton.get(cellInfo.cell());
            if (cellInfo.minesAround() > 0) {
                button.setFlagged(false);
                button.setText(String.valueOf(cellInfo.minesAround()));
            }
            button.setDisabled(true);
            if (cellInfo.cellType().equals(CellType.MINE)) {
                button.setText("*");
                button.setForeground(Color.RED);
            }
        }
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


}
