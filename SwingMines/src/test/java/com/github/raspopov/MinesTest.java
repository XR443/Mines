package com.github.raspopov;

import com.github.raspopov.model.CellButton;
import com.github.raspopov.model.Field;
import com.github.raspopov.service.CellButtonCreator;
import com.github.raspopov.service.Mines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class MinesTest {

    @Mock
    Random random;

    Mines mines;

    @BeforeEach
    void setUp() {
        mines = new Mines(random, new CellButtonCreator());
    }

    @Test
    void createField_3x3WithCenterCellSafe_FieldCreated() {
        // GIVEN
        Mockito.when(random.nextInt(3))
                // First line
                .thenReturn(0).thenReturn(0)
                .thenReturn(1).thenReturn(0)
                .thenReturn(2).thenReturn(0)
                // Second line without middle cell
                .thenReturn(0).thenReturn(1)
                .thenReturn(2).thenReturn(1)
                // Third line
                .thenReturn(0).thenReturn(2)
                .thenReturn(1).thenReturn(2)
                .thenReturn(2).thenReturn(2);

        // WHEN
        Field field = mines.createField(3, 3, .9d);

        // THEN
        assertEquals(8, field.getMines().size());
        assertFalse(field.getMines().contains(new CellButton(1, 1)));
    }

    @Test
    void createField_2x2WithRandomHalfFilled_FieldCreated() {
        // GIVEN
        Mines mines = new Mines(new CellButtonCreator());

        // WHEN
        Field field = mines.createField(2, 2, .5d);

        // THEN
        assertEquals(2, field.getMines().size());
    }
}
