package com.github.raspopov;

import com.github.raspopov.domain.*;
import com.github.raspopov.exceptions.CellOutOfBoundsException;
import com.github.raspopov.exceptions.GameNotInProgressException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    @Test
    void processMove_3x3Field_SuccessResultWithMinesAroundCount8() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new Cell(0, 0),
                        new Cell(1, 0),
                        new Cell(2, 0),
                        new Cell(0, 1),
                        new Cell(2, 1),
                        new Cell(0, 2),
                        new Cell(1, 2),
                        new Cell(2, 2)
                ));

        Cell cellToProcess = new Cell(1, 1);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertFalse(field.isGameInProgress());
        assertEquals(1, result.cellInfoList().size());
        assertEquals(new CellInfo(cellToProcess, CellType.SAFE, 8), result.cellInfoList().get(0));
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_2x1Field_FailResultWithAllMinesReturned() {
        // GIVEN
        Field field = new Field(2, 1,
                Set.of(
                        new Cell(0, 0)
                ));

        Cell cellToProcess = new Cell(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertFalse(result.success());
        assertFalse(field.isGameInProgress());
        assertEquals(1, result.cellInfoList().size());
        assertEquals(new CellInfo(cellToProcess, CellType.MINE, 0), result.cellInfoList().get(0));
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_3x3FieldEmptyCellSelected_SuccessResultWithAllSafeCellReturned() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new Cell(1, 2),
                        new Cell(2, 1),
                        new Cell(2, 2)
                ));

        Cell cellToProcess = new Cell(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertTrue(field.isGameInProgress());
        assertEquals(4, result.cellInfoList().size());

        result.cellInfoList().sort(Comparator.comparing(CellInfo::minesAround));
        assertEquals(new CellInfo(cellToProcess, CellType.SAFE, 0), result.cellInfoList().get(0));
        assertEquals(new CellInfo(new Cell(0, 1), CellType.SAFE, 1), result.cellInfoList().get(1));
        assertEquals(new CellInfo(new Cell(1, 0), CellType.SAFE, 1), result.cellInfoList().get(2));
        assertEquals(new CellInfo(new Cell(1, 1), CellType.SAFE, 3), result.cellInfoList().get(3));
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_1x1FieldCellWithIncorrectIndexes_ThrowsCellOutOfBoundsException() {
        // GIVEN
        Field field = new Field(1, 1,
                Collections.emptySet());

        Cell cellToProcess = new Cell(-1, 2);

        // WHEN
        // THEN
        assertThrows(CellOutOfBoundsException.class, () -> field.processMove(cellToProcess));
        assertEquals(0, field.getMoves().size());
    }

    @Test
    void processMove_2x1FieldWinMove_FieldIsPassedGameIsWon() {
        // GIVEN
        Field field = new Field(2, 1,
                Set.of(new Cell(1, 0)));

        Cell cellToProcess = new Cell(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertTrue(field.getWin());
    }

    @Test
    void processMove_2x1FieldMineSelected_FieldIsFailedGameIsLose() {
        // GIVEN
        Field field = new Field(2, 1,
                Set.of(new Cell(0, 0)));

        Cell cellToProcess = new Cell(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertFalse(result.success());
        assertFalse(field.getWin());
    }

    @Test
    void processMove_2x1FieldMinSelectedAndGameEnded_ThrowsGameNotInProgressException() {
        // GIVEN
        Field field = new Field(2, 1,
                Set.of(new Cell(0, 0)));

        Cell cellToProcess = new Cell(0, 0);

        field.processMove(cellToProcess);

        // WHEN
        // THEN
        assertThrows(GameNotInProgressException.class, () -> field.processMove(cellToProcess));
    }

    @Test
    void processMove_3x3FieldSelectedAllSafeCells_SuccessResultWithVictoryAndGameEnded() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new Cell(1, 2),
                        new Cell(2, 1),
                        new Cell(2, 2)
                ));

        // WHEN
        field.processMove(new Cell(0, 0));
        field.processMove(new Cell(2, 0));
        field.processMove(new Cell(0, 2));

        // THEN
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
        assertEquals(3, field.getMoves().size());
    }

    @Test
    void processMove_3x3FieldSelectedAllSafeCellsByLongOrder_SuccessResultWithVictoryAndGameEnded() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new Cell(1, 2),
                        new Cell(2, 1),
                        new Cell(2, 2)
                ));

        // WHEN
        field.processMove(new Cell(2, 0));
        field.processMove(new Cell(1, 1));
        field.processMove(new Cell(1, 0));
        field.processMove(new Cell(0, 0));
        field.processMove(new Cell(0, 2));

        // THEN
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
        assertEquals(5, field.getMoves().size());
    }


    @Test
    void processMove_3x3FieldSelectedCellWithEmptyCellsAround_SuccessResultGameNotEnded() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new Cell(2, 2)
                ));

        // WHEN
        Result result = field.processMove(new Cell(0, 0));

        // THEN
        assertTrue(result.success());
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
    }
}
