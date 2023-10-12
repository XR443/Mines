package com.github.raspopov.model;

import com.github.raspopov.exceptions.CellOutOfBoundsException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class StaticFieldTest {
    private static Random getRandom() {
        return new Random(37);
    }

    @Test
    void openCell_3x3FieldSafeCellOpened_CellOpenedMinesAroundCount8() {
        // GIVEN
        StaticField field = new StaticField(3, 3, 8, 8, getRandom());
        Cell cell = field.cellOf(1, 1);

        // WHEN
        List<Cell> openedCells = cell.open();

        // THEN
        assertEquals(8, cell.getAroundCells().size());
        assertEquals(8, cell.getMinesAround());
        assertEquals(CellType.SAFE, cell.getType());
        assertFalse(cell.isMine());
        assertTrue(cell.isOpen());

        assertTrue(field.isGameEnded());
        assertTrue(field.isGameIsWon());

        assertEquals(1, field.getOpenedCells());
        assertEquals(1, openedCells.size());
        assertEquals(cell, openedCells.get(0));
    }

    @Test
    void openCell_3x1FieldMineCellOpened_CellOpenedGameFailed() {
        // GIVEN
        StaticField field = new StaticField(3, 1, 1, 8, getRandom());
        Cell cell = field.cellOf(0, 0);
        Cell mineCell = field.cellOf(2, 0);

        // WHEN
        cell.open();
        List<Cell> openedCells = mineCell.open();

        // THEN
        assertEquals(CellType.MINE, mineCell.getType());
        assertTrue(mineCell.isMine());
        assertTrue(mineCell.isOpen());

        assertTrue(field.isGameEnded());
        assertFalse(field.isGameIsWon());

        assertEquals(2, field.getOpenedCells());
        assertEquals(1, openedCells.size());
        assertEquals(mineCell, openedCells.get(0));
    }
    @Test
    void openCell_1x1FieldOpenOutOfBoundCell_ThrowsCellOutOfBoundException() {
        // GIVEN
        StaticField field = new StaticField(1, 1, 1, 8, getRandom());

        // WHEN
        // THEN
        assertThrows(CellOutOfBoundsException.class, () -> field.cellOf(-1, 0));
        assertThrows(CellOutOfBoundsException.class, () -> field.cellOf(0, -1));
        assertThrows(CellOutOfBoundsException.class, () -> field.cellOf(1, 0));
        assertThrows(CellOutOfBoundsException.class, () -> field.cellOf(0, 1));
    }

    @Test
    void openCell_1x1FieldOpenInBoundCell_DoesNotThrowCellOutOfBoundException() {
        // GIVEN
        StaticField field = new StaticField(1, 1, 1, 8, getRandom());

        // WHEN
        // THEN
        assertDoesNotThrow(() -> field.cellOf(0, 0));
    }
}
