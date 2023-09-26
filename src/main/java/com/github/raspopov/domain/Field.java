package com.github.raspopov.domain;

import com.github.raspopov.exceptions.CellOutOfBoundsException;
import com.github.raspopov.exceptions.GameNotInProgressException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@Log4j2
public class Field {

    private final int width;

    private final int height;

    private final Set<Cell> mines;

    private final Queue<Cell> moves = new LinkedList<>();
    private final Set<Cell> openedCells = new HashSet<>();

    private boolean gameInProgress = true;

    private Boolean win;

    public Result processMove(Cell cellToProcess) {
        log.info("Start processing " + cellToProcess);
        if (cellToProcess.x() < 0 || cellToProcess.x() >= width
                || cellToProcess.y() < 0 || cellToProcess.y() >= height)
            throw new CellOutOfBoundsException("Cell X must be 0 or " + (width - 1) + " Cell Y must be 0 or " + (height - 1));

        if (!gameInProgress) {
            throw new GameNotInProgressException();
        }

        CellInfo cellInfo = getCellInfo(cellToProcess);

        moves.add(cellToProcess);

        if (cellInfo.cellType().equals(CellType.MINE)) {
            gameInProgress = false;
            win = false;
            return new Result(false, mines.stream()
                    .map(cell -> new CellInfo(cell, CellType.MINE, 0))
                    .toList());
        }

        Set<CellInfo> cellInfoSet = new HashSet<>(getNotKnownCellInfoSet(cellInfo));
        cellInfoSet.add(cellInfo);

        if (width * height - mines.size() == openedCells.size()) {
            gameInProgress = false;
            win = true;
        }

        log.info("End processing " + cellToProcess);
        return new Result(true, new LinkedList<>(cellInfoSet));
    }

    private Stream<CellInfo> getNotKnownCellInfoStream(CellInfo cellInfo) {
        List<Cell> cellsAround = getAroundCells(cellInfo.cell()).stream()
                .filter(cell -> !openedCells.contains(cell))
                .toList();
        List<CellInfo> cellInfosAround = cellsAround.stream()
                .map(this::getCellInfo)
                .toList();

        openedCells.add(cellInfo.cell());
        if (cellInfosAround.stream().anyMatch(ci -> ci.cellType().equals(CellType.MINE)))
            return Stream.of(cellInfo);
        openedCells.addAll(cellsAround);
        return Stream.concat(cellInfosAround.stream()
                        .flatMap(this::getNotKnownCellInfoStream),
                cellInfosAround.stream());
    }

    private Set<CellInfo> getNotKnownCellInfoSet(CellInfo cellInfo) {
        List<Cell> cellsAround = getAroundCells(cellInfo.cell()).stream()
                .filter(cell -> !openedCells.contains(cell))
                .toList();
        List<CellInfo> cellInfosAround = cellsAround.stream()
                .map(this::getCellInfo)
                .toList();

        openedCells.add(cellInfo.cell());
        if (cellInfosAround.stream().anyMatch(ci -> ci.cellType().equals(CellType.MINE)))
            return Set.of(cellInfo);
        openedCells.addAll(cellsAround);
        return Stream.concat(cellInfosAround.stream()
                        .flatMap(this::getNotKnownCellInfoStream),
                cellInfosAround.stream())
                .collect(Collectors.toSet());
    }

    public List<Cell> getAroundCells(Cell cell) {
        int minX = cell.x() == 0 ? 0 : cell.x() - 1;
        int minY = cell.y() == 0 ? 0 : cell.y() - 1;

        int maxX = cell.x() == width - 1 ? width - 1 : cell.x() + 1;
        int maxY = cell.y() == height - 1 ? height - 1 : cell.y() + 1;

        List<Cell> cells = new LinkedList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell newCell = new Cell(x, y);
                if (!newCell.equals(cell))
                    cells.add(newCell);
            }
        }

        return cells;
    }

    public CellInfo getCellInfo(Cell cell) {
        log.info("Get cell info for " + cell);
        CellType cellType = CellType.SAFE;

        if (mines.contains(cell))
            cellType = CellType.MINE;

        int minesAround = 0;
        for (int xi = cell.x() - 1; xi <= cell.x() + 1; xi++) {
            for (int yj = cell.y() - 1; yj <= cell.y() + 1; yj++) {
                if (!(xi == cell.x() && yj == cell.y()))
                    if (mines.contains(new Cell(xi, yj))) {
                        minesAround++;
                    }
            }
        }

        return new CellInfo(cell, cellType, minesAround);
    }
}
