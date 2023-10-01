package com.github.raspopov.model;

import com.github.raspopov.exceptions.CellOutOfBoundsException;
import com.github.raspopov.exceptions.GameNotInProgressException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    private Set<Cell> createCellsField(int width, int height) {
        int minX = 0;
        int minY = 0;

        int maxX = width - 1;
        int maxY = height - 1;

        Set<Cell> cells = new HashSet<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                cells.add(new CellButton(x, y));
            }
        }

        return cells;
    }

    @Test
    void processMove_3x3Field_SuccessResultWithMinesAroundCount8() {
        // GIVEN
        Field field = new Field(3,
                3,
                Set.of(
                        new MineCell(0, 0),
                        new MineCell(1, 0),
                        new MineCell(2, 0),
                        new MineCell(0, 1),
                        new MineCell(2, 1),
                        new MineCell(0, 2),
                        new MineCell(1, 2),
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3)
        );

        Cell cellToProcess = new CellButton(1, 1);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertFalse(field.isGameInProgress());
        assertEquals(1, result.cells().size());
        assertTrue(result.cells().get(0).getCellInfo().isPresent());
        assertEquals(new CellInfo(CellType.SAFE, 8), result.cells().get(0).getCellInfo().get());
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_2x1Field_FailResultWithAllMinesReturned() {
        // GIVEN
        Field field = new Field(2, 1,
                Set.of(
                        new MineCell(0, 0)
                ),
                createCellsField(2, 1)
        );

        Cell cellToProcess = new CellButton(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertFalse(result.success());
        assertFalse(field.isGameInProgress());
        assertEquals(1, result.cells().size());
        assertTrue(result.cells().get(0).getCellInfo().isPresent());
        assertEquals(new CellInfo(CellType.MINE, 0), result.cells().get(0).getCellInfo().get());
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_3x3FieldEmptyCellSelected_SuccessResultWithAllSafeCellReturned() {
        // GIVEN
        Field field = new Field(3, 3,
                Set.of(
                        new MineCell(1, 2),
                        new MineCell(2, 1),
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3)
        );

        Cell cellToProcess = new CellButton(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertTrue(field.isGameInProgress());
        assertEquals(4, result.cells().size());

        List<CellInfo> cellInfos = result.cells().stream()
                .map(Cell::getCellInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(CellInfo::minesAround))
                .toList();
        assertEquals(4, cellInfos.size());
        assertEquals(new CellInfo(CellType.SAFE, 0), cellInfos.get(0));
        assertEquals(new CellInfo(CellType.SAFE, 1), cellInfos.get(1));
        assertEquals(new CellInfo(CellType.SAFE, 1), cellInfos.get(2));
        assertEquals(new CellInfo(CellType.SAFE, 3), cellInfos.get(3));
        assertEquals(1, field.getMoves().size());
    }

    @Test
    void processMove_1x1FieldCellWithIncorrectIndexes_ThrowsCellOutOfBoundsException() {
        // GIVEN
        Field field = new Field(1,
                1,
                Collections.emptySet(),
                createCellsField(1, 1));

        Cell cellToProcess = new CellButton(-1, 2);

        // WHEN
        // THEN
        assertThrows(CellOutOfBoundsException.class, () -> field.processMove(cellToProcess));
        assertEquals(0, field.getMoves().size());
    }

    @Test
    void processMove_2x1FieldWinMove_FieldIsPassedGameIsWon() {
        // GIVEN
        Field field = new Field(2,
                1,
                Set.of(new MineCell(1, 0)),
                createCellsField(2, 1));

        Cell cellToProcess = new CellButton(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertTrue(result.success());
        assertTrue(field.getWin());
    }

    @Test
    void processMove_2x1FieldMineSelected_FieldIsFailedGameIsLose() {
        // GIVEN
        Field field = new Field(2,
                1,
                Set.of(new MineCell(0, 0)),
                createCellsField(2, 1));

        Cell cellToProcess = new CellButton(0, 0);

        // WHEN
        Result result = field.processMove(cellToProcess);

        // THEN
        assertFalse(result.success());
        assertFalse(field.getWin());
    }

    @Test
    void processMove_2x1FieldMinSelectedAndGameEnded_ThrowsGameNotInProgressException() {
        // GIVEN
        Field field = new Field(2,
                1,
                Set.of(new MineCell(0, 0)),
                createCellsField(2, 1));

        Cell cellToProcess = new CellButton(0, 0);

        field.processMove(cellToProcess);

        // WHEN
        // THEN
        assertThrows(GameNotInProgressException.class, () -> field.processMove(cellToProcess));
    }

    @Test
    void processMove_3x3FieldSelectedAllSafeCells_SuccessResultWithVictoryAndGameEnded() {
        // GIVEN
        Field field = new Field(3,
                3,
                Set.of(
                        new MineCell(1, 2),
                        new MineCell(2, 1),
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3));

        // WHEN
        field.processMove(new CellButton(0, 0));
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(0, 2));

        // THEN
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
        assertEquals(3, field.getMoves().size());
    }

    @Test
    void processMove_3x3FieldSelectedAllSafeCellsByLongOrder_SuccessResultWithVictoryAndGameEnded() {
        // GIVEN
        Field field = new Field(3,
                3,
                Set.of(
                        new MineCell(1, 2),
                        new MineCell(2, 1),
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3));

        // WHEN
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(1, 1));
        field.processMove(new CellButton(1, 0));
        field.processMove(new CellButton(0, 0));
        field.processMove(new CellButton(0, 2));

        // THEN
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
        assertEquals(5, field.getMoves().size());
    }

    @Test
    void processMove_3x3FieldSelectedOneSafeMultipleTimesAnd_SuccessResultGameInProgress() {
        // GIVEN
        Field field = new Field(3,
                3,
                Set.of(
                        new MineCell(1, 2),
                        new MineCell(2, 1),
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3));

        // WHEN
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(2, 0));
        field.processMove(new CellButton(2, 0));

        // THEN
        assertTrue(field.isGameInProgress());
        assertNull(field.getWin());
        assertEquals(6, field.getMoves().size());
        assertEquals(1, field.getOpenedCells());
    }

    @Test
    void processMove_3x3FieldSelectedCellWithEmptyCellsAround_SuccessResultGameNotEnded() {
        // GIVEN
        Field field = new Field(3,
                3,
                Set.of(
                        new MineCell(2, 2)
                ),
                createCellsField(3, 3));

        // WHEN
        Result result = field.processMove(new CellButton(0, 0));

        // THEN
        assertTrue(result.success());
        assertFalse(field.isGameInProgress());
        assertNotNull(field.getWin());
        assertTrue(field.getWin());
    }
}
