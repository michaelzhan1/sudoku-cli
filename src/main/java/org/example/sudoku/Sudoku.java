package org.example.sudoku;

import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

/**
 * Sudoku class to manage a game
 */
public class Sudoku {
    final static List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    final List<List<Integer>> SUBGRID_DIGITS = new ArrayList<>();
    final int MIN_REMOVE = 40;
    final int MAX_REMOVE = 50;

    int[][] grid;
    List<Set<Integer>> usedRowDigits = new ArrayList<>();
    List<Set<Integer>> usedColDigits = new ArrayList<>();
    List<Set<Integer>> usedSubgridDigits = new ArrayList<>();

    Random rand = new Random();

    /* todo: use a boolean[][] to check for any incorrect squares, so that we know
             if the puzzle is correct or not. And to check for all(finished), we should
             keep track of an int of how many blank squares remaining, as well as an int
             for how many incorrect squares, which is only updated on an update to a square
    */

    /**
     * Default constructor for Sudoku class
     * <p>
     *     Fully generates grid with random values
     * </p>
     */
    public Sudoku() {
        this(false);
    }

    /**
     * Constructor for sudoku class, with optional empty grid
     * @param empty whether the grid should be empty
     */
    public Sudoku(boolean empty) {
        initDigits();
        for (int i = 0; i < 9; i++) {
            usedRowDigits.add(new HashSet<>());
            usedColDigits.add(new HashSet<>());
            usedSubgridDigits.add(new HashSet<>());
        }
        grid = new int[9][9];

        if (!empty) {
            initDiagonalSubgrids();
            fillRemaining(0, 0);
            int removeAmount = rand.nextInt(MIN_REMOVE, MAX_REMOVE);
            removeCells(removeAmount);
        }

    }

    void clear() {
        grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            usedRowDigits.get(i).clear();
            usedColDigits.get(i).clear();
            usedSubgridDigits.get(i).clear();
        }
    }

    void setGrid(int[][] grid) {
        assert(grid.length == 9 && grid[0].length == 9) : "Incompatible grid dimensions";

        clear();

        int digit;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                digit = grid[i][j];
                if (digit != 0) {
                    this.grid[i][j] = digit;
                    usedRowDigits.get(i).add(digit);
                    usedColDigits.get(j).add(digit);
                    usedSubgridDigits.get(getSubgridIdx(i, j)).add(digit);
                }
            }
        }
    }

    /**
     * Initialize digits for each 3x3 subgrid for randomness
     * <p>
     *     Assign a random order of digits to each 3x3 subgrid to randomize grid generation
     * </p>
     */
    private void initDigits() {
        for (int i = 0; i < 9; i++) {
            Collections.shuffle(DIGITS);
            SUBGRID_DIGITS.add(DIGITS);
        }
    }

    /**
     * Print the sudoku grid
     */
    public void printGrid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(grid[i][j] != 0 ? grid[i][j] : "_");
                if (j != 8) System.out.print(" ");
                if (j == 2 || j == 5) System.out.print("| ");
            }
            System.out.println();
            if (i == 2 || i == 5) System.out.println("------+-------+------");
        }
    }

    /**
     * Initialize the sudoku grid
     * <p>
     *     First randomly initialize the diagonal sub-grids, then use a backtracking approach to fill in
     *     the remaining sub-grids. Use HashSets to optimize checking for correctness.
     * </p>
     */
    void initDiagonalSubgrids() {
        for (int i = 0; i < 9; i += 3) {
            fillSubgridWithoutRestrictions(i, i);
        }
    }

    /**
     * Fill a given subgrid without any digit restrictions
     * @param startI the top row index of the 3x3 subgrid
     * @param startJ the left col index of the 3x3 subgrid
     */
    void fillSubgridWithoutRestrictions(int startI, int startJ) {
        assert(0 <= startI && startI < 9 && 0 <= startJ && startJ < 9) : "Indices must be in-bounds";
        assert(startI % 3 == 0 && startJ % 3 == 0) : "Subgrid must be aligned to grid";
        int chosenDigit, subgridIdx;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                subgridIdx = getSubgridIdx(startI + i, startJ + j);
                chosenDigit = SUBGRID_DIGITS.get(subgridIdx).get(i * 3 + j);

                grid[startI + i][startJ + j] = chosenDigit;
                usedRowDigits.get(startI + i).add(chosenDigit);
                usedColDigits.get(startJ + j).add(chosenDigit);
                usedSubgridDigits.get(subgridIdx).add(chosenDigit);
            }
        }
    }

    /**
     * Calculate the index of the 3x3 subgrid, in row-major order
     * @param i the row index of the single cell
     * @param j the col index of the single cell
     * @return which 3x3 subgrid the cell falls in
     */
    static int getSubgridIdx(int i, int j) {
        return i / 3 * 3 + j / 3;
    }

    /**
     * Get the next index pair from a given index pair, following row-major order
     * @param i the given row index
     * @param j the given col index
     * @return the next index {i', j'}, accounting for wrapping around the grid
     */
    static int[] getNextIdx(int i, int j) {
        j++;
        if (j >= 9) {
            i++;
            j = 0;
        }
        return new int[] {i, j};
    }

    /**
     * Fill all still-empty cells in the sudoku grid
     * <p>
     *     Uses backtracking on randomized digit orders to randomize the grid
     * </p>
     * @param i the row index from which to start filling
     * @param j the col index from which to start filling
     * @return whether the remaining grid is fill-able
     */
    boolean fillRemaining(int i, int j) {
        assert(i >= 0 && j >= 0) : "Indices must be positive";
        if (i >= 9 || j >= 9) return true;

        int[] nextIdx = getNextIdx(i, j);
        if (grid[i][j] != 0) return fillRemaining(nextIdx[0], nextIdx[1]);

        int subgridIdx = getSubgridIdx(i, j);
        for (int digit : SUBGRID_DIGITS.get(subgridIdx)) {
            if (validateDigitForEmptyCell(digit, i, j)) {
                grid[i][j] = digit;
                usedRowDigits.get(i).add(digit);
                usedColDigits.get(j).add(digit);
                usedSubgridDigits.get(subgridIdx).add(digit);
                if (fillRemaining(nextIdx[0], nextIdx[1])) return true;
                grid[i][j] = 0;
                usedRowDigits.get(i).remove(digit);
                usedColDigits.get(j).remove(digit);
                usedSubgridDigits.get(subgridIdx).remove(digit);
            }
        }
        return false;
    }

    /**
     * Check if a digit would be valid in a certain cell.
     * <p>
     *     Intended use is to check whether placing a digit in an empty cell is valid.
     * </p>
     * @param digit the digit to check
     * @param i the row index of the cell
     * @param j the col index of the cell
     * @return whether a digit in a given cell is valid
     */
    boolean validateDigitForEmptyCell(int digit, int i, int j) {
        assert(0 <= i && i < 9 && 0 <= j && j < 9) : "Indices must be in-bounds";
        assert(grid[i][j] == 0) : "Cell must be empty";
        if (!usedRowDigits.get(i).contains(digit) && !usedColDigits.get(j).contains(digit)
                && !usedSubgridDigits.get(i / 3 * 3 + j / 3).contains(digit)) {
            return true;
        } else return grid[i][j] == digit;
    }

    boolean validateOccupiedCell(int i, int j) {
        assert(0 <= i && i < 9 && 0 <= j && j < 9) : "Indices must be in-bounds";
        assert(grid[i][j] != 0) : "Cell must not be 0";
        Set<Integer> row = new HashSet<>();
        Set<Integer> col = new HashSet<>();
        Set<Integer> subgrid = new HashSet<>();

        for (int idx = 0; idx < 9; idx++) {
            if (idx != i) row.add(grid[idx][j]);
            if (idx != j) col.add(grid[i][idx]);
        }

        int subgridStartI = i / 3 * 3;
        int subgridStartJ = j / 3 * 3;
        for (int ii = subgridStartI; ii < subgridStartI + 3; ii++) {
            for (int jj = subgridStartJ; jj < subgridStartJ + 3; jj++) {
                if (ii != i && jj != j) subgrid.add(grid[ii][jj]);
            }
        }

        int digit = grid[i][j];
        return !row.contains(digit) && !col.contains(digit) && !subgrid.contains(digit);
    }

    /**
     * Remove a number of cells between {@code MIN_REMOVE} and {@code MAX_REMOVE}
     * <p>
     *     Should only be used in the case that everything is validated already, otherwise
     *     unexpected behavior may occur due to the removal of a digit from a set
     * </p>
     * @param removeAmount the number of cells to remove
     */
    void removeCells(int removeAmount) {
        assert(0 <= removeAmount) : "Amount to remove should be positive";
        assert(removeAmount < 81) : "Amount to remove exceeds board size";
        List<Integer> toRemove = rand.ints(0, 81)
                .distinct()
                .limit(removeAmount)
                .boxed()
                .toList();

        int i, j, digit;
        for (int pos : toRemove) {
            i = pos / 9;
            j = pos % 9;
            digit = grid[i][j];

            grid[i][j] = 0;
            usedRowDigits.get(i).remove(digit);
            usedColDigits.get(j).remove(digit);
            usedSubgridDigits.get(getSubgridIdx(i, j)).remove(digit);
        }
    }
}
