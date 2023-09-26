package com.github.raspopov.service;

import com.github.raspopov.domain.Cell;
import com.github.raspopov.domain.Field;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class Mines {

    private final Random random;

    public Mines() {
        this(new Random());
    }

    public Mines(Random random) {
        this.random = random;
    }

    public Field createField(int width, int height, double minesPercent, Cell... cellsToExclude) {
        long numOfMines = Math.round(width * height * minesPercent);

        Set<Cell> mines = new HashSet<>((int) numOfMines);
        while (mines.size() < numOfMines) {
            Cell cell = new Cell(random.nextInt(width), random.nextInt(height));
            if (Arrays.stream(cellsToExclude).noneMatch(cell::equals))
                mines.add(cell);
        }

        return new Field(width, height, mines);
    }
}
