package com.github.raspopov.service;

import com.github.raspopov.domain.Cell;
import com.github.raspopov.domain.Field;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FieldCreator {

    private final Mines mines;

    public Field createField() {
        Field field = mines.createField(20, 20, 0.2);
//                    Field field = new Field(3, 3,
//                            Set.of(
//                                    new Cell(1, 2),
//                                    new Cell(2, 1),
//                                    new Cell(2, 2)
//                            ));
        return field;
    }

    public Field createField(int width, int height, Cell cellToExclude) {
//        Field field = new Field(3, 3,
//                Set.of(
//                        new Cell(1, 2),
//                        new Cell(2, 1),
//                        new Cell(2, 2)
//                ));
//        return field;
        return mines.createField(width, height, 0.2, cellToExclude);
    }
}
