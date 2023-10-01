package com.github.raspopov.model;

import com.github.raspopov.exceptions.CellOutOfBoundsException;
import com.github.raspopov.exceptions.GameNotInProgressException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Field {
    @Getter
    private final int width;

    @Getter
    private final int height;
    @Getter
    private final Set<Cell> mines;
    @Getter
    private Map<Cell, Cell> cells;
    @Getter
    private final Queue<Cell> moves = new LinkedList<>();
    private final AtomicInteger openedCells = new AtomicInteger(0);
    @Getter
    private boolean gameInProgress = true;
    @Getter
    private Boolean win;

    public Field(int width, int height, Set<Cell> mines) {
        this(width, height, mines, new HashSet<>());
    }

    public Field(int width, int height, Set<Cell> mines, Set<Cell> cells) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.mines.forEach(cell -> cell.setCellInfo(new CellInfo(CellType.MINE, 0)));
        this.cells = cells.stream().collect(Collectors.toMap(cell -> cell, cell -> cell));
    }

    public Result processMove(Cell cellToProcess) {
        log.info("Start processing " + cellToProcess);
        if (cellToProcess.x() < 0 || cellToProcess.x() >= width
                || cellToProcess.y() < 0 || cellToProcess.y() >= height)
            throw new CellOutOfBoundsException("Cell X must be 0 or " + (width - 1) + " Cell Y must be 0 or " + (height - 1));

        if (!gameInProgress) {
            throw new GameNotInProgressException();
        }

        CellInfo cellInfo = getCellInfo(cellToProcess);
        if (!cells.get(cellToProcess).isOpen())
            openedCells.incrementAndGet();
        cells.get(cellToProcess).open();
        cellToProcess.open();
        moves.add(cellToProcess);

        if (cellInfo.cellType().equals(CellType.MINE)) {
            gameInProgress = false;
            win = false;
            return new Result(false, mines.stream().toList());
        }

        Set<Cell> cellSet = Stream.concat(getUnknownCellInfoSet(cellToProcess), Stream.of(cellToProcess))
                .collect(Collectors.toSet());

        if (width * height - mines.size() == openedCells.get()) {
            gameInProgress = false;
            win = true;
        }

        log.info("End processing " + cellToProcess);
        return new Result(true, new LinkedList<>(cellSet));
    }

    private Stream<Cell> getUnknownCellInfoSet(Cell cell) {
        List<Cell> unOpenedCellsAround = getAroundCells(cell).stream()
                .filter(c -> !c.isOpen())
                .peek(this::getCellInfo)
                .toList();

        if (unOpenedCellsAround.stream()
                .map(Cell::getCellInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(ci -> ci.cellType().equals(CellType.MINE)))
            return Stream.of(cell);
        openedCells.addAndGet(unOpenedCellsAround.size());
        unOpenedCellsAround.forEach(Cell::open);
        return Stream.concat(unOpenedCellsAround.stream()
                        .flatMap(this::getUnknownCellInfoSet),
                unOpenedCellsAround.stream());
    }

    public List<Cell> getAroundCells(Cell cell) {
        int minX = cell.x() == 0 ? 0 : cell.x() - 1;
        int minY = cell.y() == 0 ? 0 : cell.y() - 1;

        int maxX = cell.x() == width - 1 ? width - 1 : cell.x() + 1;
        int maxY = cell.y() == height - 1 ? height - 1 : cell.y() + 1;

        List<Cell> cells = new LinkedList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell newCell = new DummyCell(x, y);
                if (!newCell.equals(cell))
                    cells.add(this.cells.get(newCell));
            }
        }

        return cells;
    }

    private CellInfo getCellInfo(Cell cell) {
        log.info("Get cell info for " + cell);

        if (cell.getCellInfo().isPresent())
            return cell.getCellInfo().get();

        CellType cellType = CellType.SAFE;

        if (mines.contains(cell))
            cellType = CellType.MINE;

        int minesAround = 0;
        for (int xi = cell.x() - 1; xi <= cell.x() + 1; xi++) {
            for (int yj = cell.y() - 1; yj <= cell.y() + 1; yj++) {
                if (!(xi == cell.x() && yj == cell.y()))
                    if (mines.contains(new DummyCell(xi, yj))) {
                        minesAround++;
                    }
            }
        }

        CellInfo cellInfo = new CellInfo(cellType, minesAround);
        cell.setCellInfo(cellInfo);
        cells.get(cell).setCellInfo(cellInfo);
        return cellInfo;
    }

    public void setCells(Set<Cell> cells) {
        this.cells = cells.stream().collect(Collectors.toMap(cell -> cell, cell -> cell));
    }

    public int getOpenedCells() {
        return openedCells.get();
    }

    private static class DummyCell extends MineCell {

        public DummyCell(int x, int y) {
            super(x, y);
        }
    }
}
