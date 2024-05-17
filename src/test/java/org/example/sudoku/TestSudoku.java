package org.example.sudoku;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@DisplayName("Sudoku Tests")
public class TestSudoku {
    Sudoku game;

    @BeforeEach
    void setup() {
        game = new Sudoku(true);
    }

    @DisplayName("Empty grid constructor")
    @Test
    void testEmptyConstructor() {
        for (int i = 0; i < 9; i++) {
            assert(Arrays.equals(game.grid[i], new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0}));
        }
        assert(game.usedRowDigits.size() == 9);
        assert(game.usedColDigits.size() == 9);
        assert(game.usedSubgridDigits.size() == 9);
        assert(game.SUBGRID_DIGITS.size() == 9);

        for (int i = 0; i < 9; i++) {
            assert(game.SUBGRID_DIGITS.get(i).size() == 9);
        }
    }

    @DisplayName("Setting grid")
    @Test
    void testSetGrid() {
        int[][] grid = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 7, 8, 9, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        game.setGrid(grid);
        for (int i = 0; i < 9; i++) {
            assert(Arrays.equals(game.grid[i], grid[i]));
        }

        assert(game.usedRowDigits.get(0).equals(new HashSet<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9))));
        assert(game.usedRowDigits.get(5).equals(new HashSet<>(List.of(1, 7, 8, 9))));

        assert(game.usedColDigits.get(0).equals(new HashSet<>(List.of(1))));
        assert(game.usedColDigits.get(1).equals(new HashSet<>(List.of(2))));
        assert(game.usedColDigits.get(2).equals(new HashSet<>(List.of(3))));
        assert(game.usedColDigits.get(3).equals(new HashSet<>(List.of(4, 7))));
        assert(game.usedColDigits.get(4).equals(new HashSet<>(List.of(5, 8))));
        assert(game.usedColDigits.get(5).equals(new HashSet<>(List.of(6, 9))));
        assert(game.usedColDigits.get(6).equals(new HashSet<>(List.of(7, 1))));
        assert(game.usedColDigits.get(7).equals(new HashSet<>(List.of(8))));
        assert(game.usedColDigits.get(8).equals(new HashSet<>(List.of(9))));

        assert(game.usedSubgridDigits.get(0).equals(new HashSet<>(List.of(1, 2, 3))));
        assert(game.usedSubgridDigits.get(1).equals(new HashSet<>(List.of(4, 5, 6))));
        assert(game.usedSubgridDigits.get(2).equals(new HashSet<>(List.of(7, 8, 9))));
        assert(game.usedSubgridDigits.get(4).equals(new HashSet<>(List.of(7, 8, 9))));
        assert(game.usedSubgridDigits.get(5).equals(new HashSet<>(List.of(1))));
    }

    @DisplayName("Get next index")
    @Test
    void testGetNextIdx() {
        assert(Arrays.equals(new int[] {1, 3}, Sudoku.getNextIdx(1, 2)));
        assert(Arrays.equals(new int[] {0, 1}, Sudoku.getNextIdx(0, 0)));
        assert(Arrays.equals(new int[] {1, 0}, Sudoku.getNextIdx(0, 8)));
    }

    @DisplayName("Diagonal subgrids")
    @Test
    void testInitDiagonalSubgrids() {
        game.initDiagonalSubgrids();
        for (int i = 0; i < 9; i++) {
            assert(game.usedRowDigits.get(i).size() == 3);
            assert(game.usedColDigits.get(i).size() == 3);
        }

        for (int i = 0; i < 9; i++) {
            if (i % 4 == 0) assert(game.usedSubgridDigits.get(i).size() == 9);
            else assert(game.usedSubgridDigits.get(i).isEmpty());
        }
    }
}
