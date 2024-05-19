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
            assert(Arrays.equals(game.isStarterClue[i],
                    new boolean[] {false, false, false, false, false, false, false, false, false}));
        }
        assert(game.generationUsedRowDigits.size() == 9);
        assert(game.generationUsedColDigits.size() == 9);
        assert(game.generationUsedSubgridDigits.size() == 9);
        assert(game.SUBGRID_DIGITS.size() == 9);
        assert(game.blankCount == 81);

        for (int i = 0; i < 9; i++) {
            assert(game.SUBGRID_DIGITS.get(i).size() == 9);
        }
    }

    @DisplayName("Full grid constructor")
    @Test
    void testConstructor() {
        Sudoku game = new Sudoku();
        int blankSpaces = 0;
        int starterSpaces = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.checkCell(i, j));
                if (game.grid[i][j] == 0) blankSpaces++;
                if (game.isStarterClue[i][j]) starterSpaces++;
            }
        }
        assert(game.blankCount == blankSpaces);
        assert(81 - game.blankCount == starterSpaces);
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
            assert(game.generationUsedRowDigits.get(i).isEmpty());
            assert(game.generationUsedColDigits.get(i).isEmpty());
            assert(game.generationUsedSubgridDigits.get(i).isEmpty());
        }
        assert(game.blankCount == 81);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(!game.isStarterClue[i][j]);
            }
        }
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

    @DisplayName("Check finished")
    @Test
    void testCheckFinished() {
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
        // valid game
        game.setGrid(grid);
        assert(game.checkFinished());

        // some blank values
        grid[0][0] = 0;
        game.setGrid(grid);
        assert(!game.checkFinished());

        // invalid cells
        grid[0][0] = 3;
        assert(!game.checkFinished());
    }

    @DisplayName("Check cell")
    @Test
    void testCheckCell() {
        int[][] grid = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 7, 8, 9, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        // valid tests
        game.setGrid(grid);
        assert(game.checkCell(0, 0));
        assert(game.checkCell(1, 0));

        // invalid after player set by row
        game.enterDigit(7, 5, 2);
        assert(!game.checkCell(5, 2));
        game.enterDigit(0, 5, 2);
        assert(game.checkCell(5, 2));

        // invalid after player-set by col
        game.enterDigit(1, 1, 0);
        assert(!game.checkCell(1, 0));
        game.enterDigit(0, 1, 0);
        assert(game.checkCell(1, 0));

        // invalid after player-set by subgrid
        game.enterDigit(4, 1, 4);
        assert(!game.checkCell(1, 4));
        game.enterDigit(0, 1, 4);
        assert(game.checkCell(1, 4));

        // invalid from input grid
        grid[0][0] = 2;
        game.setGrid(grid);
        assert(!game.checkCell(0, 0));
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

        int starterCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.isStarterClue[i][j]) starterCount++;
            }
        }
        assert(starterCount == 13);
        assert(game.blankCount == 68);

        assert(game.generationUsedRowDigits.get(0).equals(new HashSet<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9))));
        assert(game.generationUsedRowDigits.get(5).equals(new HashSet<>(List.of(1, 7, 8, 9))));

        assert(game.generationUsedColDigits.get(0).equals(new HashSet<>(List.of(1))));
        assert(game.generationUsedColDigits.get(1).equals(new HashSet<>(List.of(2))));
        assert(game.generationUsedColDigits.get(2).equals(new HashSet<>(List.of(3))));
        assert(game.generationUsedColDigits.get(3).equals(new HashSet<>(List.of(4, 7))));
        assert(game.generationUsedColDigits.get(4).equals(new HashSet<>(List.of(5, 8))));
        assert(game.generationUsedColDigits.get(5).equals(new HashSet<>(List.of(6, 9))));
        assert(game.generationUsedColDigits.get(6).equals(new HashSet<>(List.of(7, 1))));
        assert(game.generationUsedColDigits.get(7).equals(new HashSet<>(List.of(8))));
        assert(game.generationUsedColDigits.get(8).equals(new HashSet<>(List.of(9))));

        assert(game.generationUsedSubgridDigits.get(0).equals(new HashSet<>(List.of(1, 2, 3))));
        assert(game.generationUsedSubgridDigits.get(1).equals(new HashSet<>(List.of(4, 5, 6))));
        assert(game.generationUsedSubgridDigits.get(2).equals(new HashSet<>(List.of(7, 8, 9))));
        assert(game.generationUsedSubgridDigits.get(4).equals(new HashSet<>(List.of(7, 8, 9))));
        assert(game.generationUsedSubgridDigits.get(5).equals(new HashSet<>(List.of(1))));

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
            assert(game.generationUsedRowDigits.get(i).isEmpty());
            assert(game.generationUsedColDigits.get(i).isEmpty());
            assert(game.generationUsedSubgridDigits.get(i).isEmpty());
        }
        assert(game.blankCount == 81);

        starterCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.grid[i][j] == 0);
                if (game.isStarterClue[i][j]) starterCount++;
            }
        }
        assert(starterCount == 0);

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

    @DisplayName("Diagonal subgrids")
    @Test
    void testInitDiagonalSubgrids() {
        game.initDiagonalSubgrids();
        for (int i = 0; i < 9; i++) {
            assert(game.generationUsedRowDigits.get(i).size() == 3);
            assert(game.generationUsedColDigits.get(i).size() == 3);
        }

        for (int i = 0; i < 9; i++) {
            if (i % 4 == 0) assert(game.generationUsedSubgridDigits.get(i).size() == 9);
            else assert(game.generationUsedSubgridDigits.get(i).isEmpty());
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(i / 3 != j / 3 || (game.isStarterClue[i][j]));
            }
        }
        assert(game.blankCount == 54);
    }

    @DisplayName("Fill single subgrid")
    @Test
    void testFillSubgridWithoutRestrictions() {
        game.fillSubgridWithoutRestrictions(0, 0);
        for (int i = 0; i < 3; i++) {
            assert(game.generationUsedRowDigits.get(i).size() == 3);
            assert(game.generationUsedColDigits.get(i).size() == 3);
        }
        assert(game.generationUsedSubgridDigits.getFirst().size() == 9);

        game.fillSubgridWithoutRestrictions(6, 3);
        for (int i = 0; i < 3; i++) {
            assert(game.generationUsedRowDigits.get(i + 6).size() == 3);
            assert(game.generationUsedColDigits.get(i + 3).size() == 3);
        }
        assert(game.generationUsedSubgridDigits.get(7).size() == 9);

        int starterCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.isStarterClue[i][j]) starterCount++;
            }
        }
        assert(starterCount == 18);
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
        assert(game.isStarterClue[8][8]);
        assert(game.blankCount == 0);

        game.clear();
        boolean temp = game.fillRemaining(0, 0);
        assert(temp);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assert(game.grid[i][j] != 0);
            }
        }

        for (int i = 0; i < 9; i++) {
            assert(game.generationUsedRowDigits.get(i).size() == 9);
            assert(game.generationUsedColDigits.get(i).size() == 9);
            assert(game.generationUsedSubgridDigits.get(i).size() == 9);
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
        int starterSpaces = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.grid[i][j] == 0) emptyCount++;
                if (game.isStarterClue[i][j]) starterSpaces++;
            }
        }
        assert(emptyCount == 20);
        assert(emptyCount == game.blankCount);
        assert(starterSpaces == 61);

        game.setGrid(grid);
        AssertionError outOfBoundsEx1 = Assertions.assertThrows(AssertionError.class,
                () -> game.removeRandomCells(-1));
        AssertionError outOfBoundsEx2 = Assertions.assertThrows(AssertionError.class,
                () -> game.removeRandomCells(81));

        Assertions.assertEquals("Amount to remove should be positive", outOfBoundsEx1.getMessage());
        Assertions.assertEquals("Amount to remove exceeds board size", outOfBoundsEx2.getMessage());
    }

    @DisplayName("Enter digit")
    @Test
    void testEnterDigit() {
        game.enterDigit(1, 0, 0);
        assert(game.grid[0][0] == 1);
        assert(game.blankCount == 80);
        game.enterDigit(0, 0, 0);
        assert(game.grid[0][0] == 0);
        assert(game.blankCount == 81);

        int[][] grid = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 7, 8, 9, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        game.setGrid(grid);
        game.enterDigit(2, 0, 0);
        assert(game.grid[0][0] == 1);
        assert(game.blankCount == 68);
        game.enterDigit(2, 1, 0);
        assert(game.grid[1][0] == 2);
        assert(game.blankCount == 67);
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
