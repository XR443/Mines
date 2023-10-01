package com.github.raspopov.service;

import com.github.raspopov.model.Cell;
import com.github.raspopov.model.CellButton;
import org.springframework.stereotype.Component;

@Component
public class CellButtonCreator implements CellCreator {
    @Override
    public Cell createCell(int x, int y) {
        return new CellButton(x, y);
    }
}
