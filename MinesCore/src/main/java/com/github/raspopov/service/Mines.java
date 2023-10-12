package com.github.raspopov.service;


import com.github.raspopov.model.StaticField;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Mines {
    private final Random random;

    public Mines() {
        this(new Random());
    }

    public Mines(Random random) {
        this.random = random;
    }

    public StaticField createField(int width, int height, double minesPercent) {
        long numOfMines = Math.round(width * height * minesPercent);

        return new StaticField(width, height, (int) numOfMines, random);
    }
}
