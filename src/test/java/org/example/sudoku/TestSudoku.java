package org.example.sudoku;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
            assert(Arrays.equals(game.correct[i],
                    new boolean[] {false, false, false, false, false, false, false, false, false}));
        }
        assert(game.usedRowDigits.size() == 9);
        assert(game.usedColDigits.size() == 9);
        assert(game.usedSubgridDigits.size() == 9);
        assert(game.SUBGRID_DIGITS.size() == 9);
        assert(game.incorrectCount == 81);
        assert(game.blankCount == 81);

        for (int i = 0; i < 9; i++) {
            assert(game.SUBGRID_DIGITS.get(i).size() == 9);
        }
    }

    @DisplayName("Full grid constructor")
    @Test
    void testConstructor() {
        Sudoku game = new Sudoku();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.grid[i][j] == 0 || (game.validateOccupiedCell(i, j)));
            }
        }
    }

    @DisplayName("Clear")
    @Test
    void testClear() {
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
        game.clear();
        for (int i = 0; i < 9; i++) {
            assert(Arrays.equals(game.grid[i], new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0}));
            assert(game.usedRowDigits.get(i).isEmpty());
            assert(game.usedColDigits.get(i).isEmpty());
            assert(game.usedSubgridDigits.get(i).isEmpty());
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(!game.correct[i][j]);
            }
        }
        assert(game.incorrectCount == 81);
        assert(game.blankCount == 81);
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

        int correctCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.correct[i][j]) correctCount++;
            }
        }
        assert(correctCount == 13);

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

        assert(game.incorrectCount == 68);
        assert(game.blankCount == 68);

        grid = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        game.setGrid(grid);
        for (int i = 0; i < 9; i++) {
            assert(game.usedRowDigits.get(i).isEmpty());
            assert(game.usedColDigits.get(i).isEmpty());
            assert(game.usedSubgridDigits.get(i).isEmpty());
        }

        assert(game.incorrectCount == 81);
        assert(game.blankCount == 81);

        correctCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.grid[i][j] == 0);
                if (game.correct[i][j]) correctCount++;
            }
        }
        assert(correctCount == 0);

        int[][] badGrid1 = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        int[][] badGrid2 = {
                {0},
                {0},
                {0},
                {0},
                {0},
                {0},
                {0},
                {0},
                {0}
        };
        AssertionError badDimEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.setGrid(badGrid1));
        AssertionError badDimEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.setGrid(badGrid2));
        assert("Incompatible grid dimensions".equals(badDimEx1.getMessage()));
        assert("Incompatible grid dimensions".equals(badDimEx2.getMessage()));
    }

    @DisplayName("Printing grid")
    @Test
    void testPrintGrid() {
        PrintStream stdout = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

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
        game.printGrid();

        String expected = """
            1 2 3 | 4 5 6 | 7 8 9\r
            _ _ _ | _ _ _ | _ _ _\r
            _ _ _ | _ _ _ | _ _ _\r
            ------+-------+------\r
            _ _ _ | _ _ _ | _ _ _\r
            _ _ _ | _ _ _ | _ _ _\r
            _ _ _ | 7 8 9 | 1 _ _\r
            ------+-------+------\r
            _ _ _ | _ _ _ | _ _ _\r
            _ _ _ | _ _ _ | _ _ _\r
            _ _ _ | _ _ _ | _ _ _\r
            """;
        assert(expected.equals(outputStream.toString()));
        System.setOut(stdout);
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

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(i / 3 != j / 3 || (game.correct[i][j]));
            }
        }

        assert(game.incorrectCount == 54);
        assert(game.blankCount == 54);
    }

    @DisplayName("Fill single subgrid")
    @Test
    void testFillSubgridWithoutRestrictions() {
        game.fillSubgridWithoutRestrictions(0, 0);
        for (int i = 0; i < 3; i++) {
            assert(game.usedRowDigits.get(i).size() == 3);
            assert(game.usedColDigits.get(i).size() == 3);
        }
        assert(game.usedSubgridDigits.getFirst().size() == 9);

        game.fillSubgridWithoutRestrictions(6, 3);
        for (int i = 0; i < 3; i++) {
            assert(game.usedRowDigits.get(i + 6).size() == 3);
            assert(game.usedColDigits.get(i + 3).size() == 3);
        }
        assert(game.usedSubgridDigits.get(7).size() == 9);

        int correctCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.correct[i][j]) correctCount++;
            }
        }
        assert(correctCount == 18);
        assert(game.incorrectCount == 63);
        assert(game.blankCount == 63);

        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(9, 0));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(-3, 0));
        AssertionError outOfBoundsEx3 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(0, 9));
        AssertionError outOfBoundsEx4 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(0, -3));

        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx2.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx3.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx4.getMessage());

        AssertionError nonalignedEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(1, 0));
        AssertionError nonalignedEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillSubgridWithoutRestrictions(0, 1));
        Assertions.assertEquals("Subgrid must be aligned to grid", nonalignedEx1.getMessage());
        Assertions.assertEquals("Subgrid must be aligned to grid", nonalignedEx2.getMessage());
    }

    @DisplayName("Fill remaining")
    @Test
    void testFillRemaining() {
        int[][] grid = {
                {8, 3, 2, 1, 6, 9, 4, 5, 7},
                {6, 1, 4, 3, 5, 7, 2, 8, 9},
                {9, 7, 5, 2, 4, 8, 3, 6, 1},
                {1, 4, 7, 8, 3, 2, 5, 9, 6},
                {5, 8, 9, 6, 1, 4, 7, 2, 3},
                {3, 2, 6, 9, 7, 5, 1, 4, 8},
                {4, 5, 1, 7, 9, 6, 8, 3, 2},
                {7, 9, 8, 5, 2, 3, 6, 1, 4},
                {2, 6, 3, 4, 8, 1, 9, 7, 0}
        };
        game.setGrid(grid);
        game.fillRemaining(8, 8);
        assert(game.grid[8][8] == 5);

        game.clear();
        boolean temp = game.fillRemaining(0, 0);
        assert(temp);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.grid[i][j] != 0);
            }
        }

        for (int i = 0; i < 9; i++) {
            assert(game.usedRowDigits.get(i).size() == 9);
            assert(game.usedColDigits.get(i).size() == 9);
            assert(game.usedSubgridDigits.get(i).size() == 9);
        }

        temp = game.fillRemaining(10, 0);
        assert(temp);
        temp = game.fillRemaining(0, 10);
        assert(temp);

        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillRemaining(-1, 0));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.fillRemaining(0, -1));

        Assertions.assertEquals("Indices must be positive", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Indices must be positive", outOfBoundsEx2.getMessage());
    }

    @DisplayName("Validate empty cell")
    @Test
    void testValidateDigitForEmptyCell() {
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
        assert(game.validateDigitForEmptyCell(4, 1, 0));
        assert(game.validateDigitForEmptyCell(4, 5, 2));
        assert(!game.validateDigitForEmptyCell(4, 6, 3));
        assert(!game.validateDigitForEmptyCell(7, 5, 2));
        assert(!game.validateDigitForEmptyCell(8, 4, 5));

        AssertionError nonEmptyEx = Assertions.assertThrows(AssertionError.class,
                                    () -> game.validateDigitForEmptyCell(1, 0, 0));
        Assertions.assertEquals("Cell must be empty", nonEmptyEx.getMessage());

        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateDigitForEmptyCell(1, 9, 0));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateDigitForEmptyCell(1, -1, 0));
        AssertionError outOfBoundsEx3 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateDigitForEmptyCell(1, 0, 9));
        AssertionError outOfBoundsEx4 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateDigitForEmptyCell(1, 0, -1));

        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx2.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx3.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx4.getMessage());
    }

    @DisplayName("Validate occupied cell")
    @Test
    void testValidateOccupiedCell() {
        int[][] grid = {
                {8, 3, 2, 1, 6, 9, 4, 5, 7},
                {6, 1, 4, 3, 5, 7, 2, 8, 9},
                {9, 7, 5, 2, 4, 8, 3, 6, 1},
                {1, 4, 7, 8, 3, 2, 5, 9, 6},
                {5, 8, 9, 6, 1, 4, 7, 2, 3},
                {3, 2, 6, 9, 7, 5, 1, 4, 8},
                {4, 5, 1, 7, 9, 6, 8, 3, 2},
                {7, 9, 8, 5, 2, 3, 6, 1, 4},
                {2, 6, 3, 4, 8, 1, 9, 7, 5}
        };
        game.setGrid(grid);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.validateOccupiedCell(i, j));
            }
        }

        grid[0][0] = 3;
        game.setGrid(grid);
        assert(!game.validateOccupiedCell(0, 0));

        grid[0][0] = 0;
        game.setGrid(grid);
        AssertionError ex = Assertions.assertThrows(AssertionError.class,
                                            () -> game.validateOccupiedCell(0, 0));
        Assertions.assertEquals("Cell must not be 0", ex.getMessage());
        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateOccupiedCell(9, 0));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateOccupiedCell(-1, 0));
        AssertionError outOfBoundsEx3 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateOccupiedCell(0, 9));
        AssertionError outOfBoundsEx4 = Assertions.assertThrows(AssertionError.class,
                () -> game.validateOccupiedCell(0, -1));

        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx2.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx3.getMessage());
        Assertions.assertEquals("Indices must be in-bounds", outOfBoundsEx4.getMessage());
    }

    @DisplayName("Remove cells")
    @Test
    void testRemoveRandomCells() {
        int[][] grid = {
                {8, 3, 2, 1, 6, 9, 4, 5, 7},
                {6, 1, 4, 3, 5, 7, 2, 8, 9},
                {9, 7, 5, 2, 4, 8, 3, 6, 1},
                {1, 4, 7, 8, 3, 2, 5, 9, 6},
                {5, 8, 9, 6, 1, 4, 7, 2, 3},
                {3, 2, 6, 9, 7, 5, 1, 4, 8},
                {4, 5, 1, 7, 9, 6, 8, 3, 2},
                {7, 9, 8, 5, 2, 3, 6, 1, 4},
                {2, 6, 3, 4, 8, 1, 9, 7, 5}
        };
        game.setGrid(grid);

        game.removeRandomCells(20);
        int emptyCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.grid[i][j] == 0) emptyCount++;
            }
        }
        assert(emptyCount == 20);

        game.setGrid(grid);
        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.removeRandomCells(-1));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.removeRandomCells(81));

        Assertions.assertEquals("Amount to remove should be positive", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Amount to remove exceeds board size", outOfBoundsEx2.getMessage());
    }

    @DisplayName("Get subgrid index")
    @Test
    void testGetSubgridIdx() {
        assert(Sudoku.getSubgridIdx(0, 0) == 0);
        assert(Sudoku.getSubgridIdx(3, 0) == 3);
        assert(Sudoku.getSubgridIdx(6, 6) == 8);
    }

    @DisplayName("Get next index")
    @Test
    void testGetNextIdx() {
        assert(Arrays.equals(new int[] {1, 3}, Sudoku.getNextIdx(1, 2)));
        assert(Arrays.equals(new int[] {0, 1}, Sudoku.getNextIdx(0, 0)));
        assert(Arrays.equals(new int[] {1, 0}, Sudoku.getNextIdx(0, 8)));
    }

}
