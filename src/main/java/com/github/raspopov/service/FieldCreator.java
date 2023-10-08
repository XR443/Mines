package com.github.raspopov.service;

import com.github.raspopov.model.Cell;
import com.github.raspopov.model.Field;
import com.github.raspopov.utils.FlaggedMinesCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class FieldCreator {

    private final Mines mines;
    private final FlaggedMinesCount flaggedMinesCount;

    public Field createField(int width, int height, Set<Cell> generatedCells, Cell cellToExclude) {
//        Field field = new Field(3, 3,
//                Set.of(
//                        new Cell(1, 2),
//                        new Cell(2, 1),
//                        new Cell(2, 2)
//                ));
//        return field;
        Field field = mines.createField(width, height, 0.2, generatedCells, cellToExclude);
        flaggedMinesCount.setMinesCount(field.getMines().size());
        return field;
    }
}
