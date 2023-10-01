package com.github.raspopov.service;

import com.github.raspopov.model.Cell;

public interface CellCreator {
    Cell createCell(int x, int y);
}
