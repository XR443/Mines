package com.github.raspopov.service;

import com.github.raspopov.model.StaticField;
import com.github.raspopov.utils.FlaggedMinesCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FieldCreator {

    private final Mines mines;
    private final FlaggedMinesCount flaggedMinesCount;

    public StaticField createField(int width, int height) {
//        StaticField field = new StaticField(3, 3,3, new Random(37));
        StaticField field = mines.createField(width, height, 0.2d);
        flaggedMinesCount.setMinesCount(field.getMinesCount());
        return field;
    }
}
