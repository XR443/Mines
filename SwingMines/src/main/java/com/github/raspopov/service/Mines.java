package com.github.raspopov.service;

import com.github.raspopov.model.Cell;
import com.github.raspopov.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class Mines {

    private final Random random;
    private final CellCreator cellCreator;

    @Autowired
    public Mines(CellCreator cellCreator) {
        this(new Random(), cellCreator);
    }

    public Mines(Random random, CellCreator cellCreator) {
        this.random = random;
        this.cellCreator = cellCreator;
    }

    public Field createField(int width, int height,
                             double minesPercent,
                             Set<Cell> generatedCells,
                             Cell... cellsToExclude) {
        Field field = createField(width, height, minesPercent, cellsToExclude);
        field.setCells(generatedCells);
        return field;
    }

    public Field createField(int width, int height,
                             double minesPercent,
                             Cell... cellsToExclude) {
        long numOfMines = Math.round(width * height * minesPercent);

        Set<Cell> mines = new HashSet<>((int) numOfMines);
        while (mines.size() < numOfMines) {
            Cell cell = cellCreator.createCell(random.nextInt(width), random.nextInt(height));
            if (Arrays.stream(cellsToExclude).noneMatch(cell::equals))
                mines.add(cell);
        }

        return new Field(width, height, mines);
    }

    private Set<Cell> createCellsField(int width, int height) {
        int minX = 0;
        int minY = 0;

        int maxX = width - 1;
        int maxY = height - 1;

        Set<Cell> cells = new HashSet<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                cells.add(cellCreator.createCell(x, y));
            }
        }

        return cells;
    }
}
