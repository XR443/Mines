package com.github.raspopov.model;

import com.github.raspopov.exceptions.CellOutOfBoundsException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Log4j2
public class StaticField {

    private final static int MAX_MINES_COUNT_PER_CELL_DEFAULT = 6;

    private final int maxMinesCountPerCell;
    @Getter
    private final int width;
    @Getter
    private final int height;
    private final AtomicInteger openedCells;
    private final AtomicInteger safeCells;
    private final AtomicInteger minesCreated;
    @Getter
    private final int minesCount;

    private final Map<Integer, Cell> cellMap;
    private final Random random;

    @Getter
    private boolean gameStarted;
    @Getter
    private boolean gameEnded;
    @Getter
    private boolean gameIsWon;

    public StaticField(int width, int height, int minesCount) {
        this(width, height, minesCount, MAX_MINES_COUNT_PER_CELL_DEFAULT);
    }

    public StaticField(int width, int height, int minesCount, Random random) {
        this(width, height, minesCount, MAX_MINES_COUNT_PER_CELL_DEFAULT, random);
    }

    public StaticField(int width, int height, int minesCount, int maxMinesCountPerCell) {
        this(width, height, minesCount, maxMinesCountPerCell, new Random());
    }

    public StaticField(int width, int height, int minesCount, int maxMinesCountPerCell, Random random) {
        this(width, height, minesCount, maxMinesCountPerCell, new HashMap<>(), random);
    }

    public StaticField(int width, int height, int minesCount, int maxMinesCountPerCell, Map<Integer, Cell> cellMap, Random random) {
        this.width = width;
        this.height = height;
        this.cellMap = cellMap;
        this.minesCount = minesCount;
        this.maxMinesCountPerCell = maxMinesCountPerCell;
        this.openedCells = new AtomicInteger(0);
        this.safeCells = new AtomicInteger(0);
        this.minesCreated = new AtomicInteger(0);
        this.random = random;
    }

    public Cell cellOf(int x, int y) {
        if (cellMap.isEmpty()) {
            gameStarted();

            return createCell(x, y);
        }
        return getCell(x, y);
    }

    private void gameStarted() {
        gameEnded = false;
        gameStarted = true;
    }

    private Cell createCell(int x, int y) {
        return createCell(x, y, 0);
    }

    private Cell createCell(int x, int y, int minesCreated) {
        if (x < 0 || x > (width - 1))
            throw new CellOutOfBoundsException("X must be from 0 to "+(width - 1));

        if (y < 0 || y > (height - 1))
            throw new CellOutOfBoundsException("Y must be from 0 to "+(height - 1));

        Cell cell = getCell(x, y);
        if (cell != null) {
            return cell;
        }

        Cell newCell;
        if (cellMap.isEmpty()) {
            safeCells.incrementAndGet();
            newCell = new Cell(x, y, CellType.SAFE, this);
        } else {
            newCell = new Cell(x, y, getCellType(minesCreated), this);
        }
        putCell(newCell);

        newCell.initializeAroundCells();

        return newCell;
    }

    private void putCell(Cell cell) {
        cellMap.put(Objects.hash(cell.x(), cell.y()), cell);
    }

    private Cell getCell(int x, int y) {
        return cellMap.get(Objects.hash(x, y));
    }

    private Cell getCell(Cell cell) {
        return cellMap.get(Objects.hash(cell.x(), cell.y()));
    }

    protected List<Cell> getAroundCells(Cell cell) {
        int minX = cell.x() == 0 ? 0 : cell.x() - 1;
        int minY = cell.y() == 0 ? 0 : cell.y() - 1;

        int maxX = cell.x() == width - 1 ? width - 1 : cell.x() + 1;
        int maxY = cell.y() == height - 1 ? height - 1 : cell.y() + 1;

        List<Cell> cells = new LinkedList<>();
        int minesCreated = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell newCell = createCell(x, y, minesCreated);
                if (!newCell.equals(cell)) {
                    cells.add(newCell);
                    if (newCell.getType().equals(CellType.MINE))
                        minesCreated += 1;
                }
            }
        }
        return cells;
    }

    private CellType getCellType() {
        return getCellType(0);
    }


    private CellType getCellType(int minesCreated) {
        if (minesCreated > maxMinesCountPerCell) {
            safeCells.incrementAndGet();
            return CellType.SAFE;
        }

        if (this.minesCreated.get() < minesCount) {
            double mineChance = random.nextDouble();
            int fieldSize = width * height;
            int cellsForMines = fieldSize - safeCells.get() - this.minesCreated.get();
            int remainingMines = minesCount - this.minesCreated.get();
            double mineChancePerCell = (double) remainingMines / (double) cellsForMines;
            if (mineChance < mineChancePerCell) {
                this.minesCreated.incrementAndGet();
                return CellType.MINE;
            }
        }

        safeCells.incrementAndGet();
        return CellType.SAFE;
    }

    public List<Cell> openCell(Cell cell) {
        if (cell.isMine()) {
            gameEnded = true;
            gameIsWon = false;
        } else {
            int openedCells = this.openedCells.incrementAndGet();
            int fieldSize = width * height;
            int safeCellsCount = fieldSize - minesCount;
            if (openedCells == safeCellsCount) {
                gameEnded = true;
                gameIsWon = true;
            }
        }

        if (gameEnded && !gameIsWon) {
            return cellMap.values().stream()
                    .filter(Cell::isMine)
                    .toList();
        }

        if (cell.getMinesAround() > 0)
            return List.of(cell);

        return Stream.concat(Stream.of(cell), cell.getAroundCells().stream()
                        .filter(c -> !c.isOpen())
                        .flatMap(c -> c.open().stream()))
                .toList();
    }

    public int getOpenedCells() {
        return openedCells.get();
    }

    public boolean isGameInProgress() {
        return gameStarted && !gameEnded;
    }
}
