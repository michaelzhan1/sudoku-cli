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
    // Constants
    final static List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    final List<List<Integer>> SUBGRID_DIGITS = new ArrayList<>();
    final static int MIN_REMOVE = 40;
    final static int MAX_REMOVE = 50;

    // Grid generation helper structures
    List<Set<Integer>> generationUsedRowDigits = new ArrayList<>();
    List<Set<Integer>> generationUsedColDigits = new ArrayList<>();
    List<Set<Integer>> generationUsedSubgridDigits = new ArrayList<>();

    // Grid tracking variables
    int[][] grid;
    boolean[][] isStarterClue; // marks all clues that were initialized automatically, should not be modified outside of generation
    int blankCount;

    // Random
    Random rand = new Random();

    // ===== CONSTRUCTORS =====
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
            generationUsedRowDigits.add(new HashSet<>());
            generationUsedColDigits.add(new HashSet<>());
            generationUsedSubgridDigits.add(new HashSet<>());
        }
        grid = new int[9][9];
        isStarterClue = new boolean[9][9];
        blankCount = 81;

        if (!empty) {
            initDiagonalSubgrids();
            fillRemaining(0, 0);
            int removeAmount = rand.nextInt(MIN_REMOVE, MAX_REMOVE);
            removeRandomCells(removeAmount);
        }
    }
    // ===== END CONSTRUCTORS =====


    // ===== GENERAL METHODS =====
    /**
     * Clear the grid and associated attributes
     */
    void clear() {
        grid = new int[9][9];
        isStarterClue = new boolean[9][9];
        blankCount = 81;
        for (int i = 0; i < 9; i++) {
            generationUsedRowDigits.get(i).clear();
            generationUsedColDigits.get(i).clear();
            generationUsedSubgridDigits.get(i).clear();
        }
    }

    /**
     * Print the sudoku grid
     */
    public void printGrid() { // "\u001b[34m for blue, used for user-input numbers"
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
     * Check if the sudoku is solved
     * @return if the sudoku is solved
     */
    public boolean checkFinished() { // todo: add test
        if (blankCount != 0) return false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!checkCell(i, j)) return false;
            }
        }
        return true;
    }

    /**
     * Check if a certain cell is valid
     * @param i the row index of the cell
     * @param j the col index of the cell
     * @return whether the cell is valid
     */
    boolean checkCell(int i, int j) {
        int digit = grid[i][j];
        if (digit == 0) return true;

        for (int k = 0; k < 9; k++) {
            if ((grid[i][k] == digit && k != j) || (grid[k][j] == digit && k != i)) return false;
        }

        int subgridStartI = i / 3 * 3;
        int subgridStartJ = j / 3 * 3;
        for (int ii = subgridStartI; ii < subgridStartI + 3; ii++) {
            for (int jj = subgridStartJ; jj < subgridStartJ + 3; jj++) {
                if ((ii != i || jj != j) && grid[ii][jj] == digit) return false;
            }
        }
        return true;
    }
    // ===== END GENERAL METHODS


    // ===== GENERATION METHODS =====
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
     * Set the grid attribute to a given 9x9 grid
     * <p>
     *     Note that this should be used with a valid sudoku grid.
     * </p>
     * @param grid the grid to set
     */
    void setGrid(int[][] grid) {
        assert(grid.length == 9 && grid[0].length == 9) : "Incompatible grid dimensions";

        clear();

        int digit;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                digit = grid[i][j];
                if (digit != 0) {
                    this.grid[i][j] = digit;
                    generationUsedRowDigits.get(i).add(digit);
                    generationUsedColDigits.get(j).add(digit);
                    generationUsedSubgridDigits.get(getSubgridIdx(i, j)).add(digit);

                    isStarterClue[i][j] = true;
                    blankCount--;
                }
            }
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
                generationUsedRowDigits.get(startI + i).add(chosenDigit);
                generationUsedColDigits.get(startJ + j).add(chosenDigit);
                generationUsedSubgridDigits.get(subgridIdx).add(chosenDigit);

                isStarterClue[startI + i][startJ + j] = true;
                blankCount--;
            }
        }
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
                generationUsedRowDigits.get(i).add(digit);
                generationUsedColDigits.get(j).add(digit);
                generationUsedSubgridDigits.get(subgridIdx).add(digit);
                isStarterClue[i][j] = true;
                blankCount--;

                if (fillRemaining(nextIdx[0], nextIdx[1])) return true;
                grid[i][j] = 0;
                generationUsedRowDigits.get(i).remove(digit);
                generationUsedColDigits.get(j).remove(digit);
                generationUsedSubgridDigits.get(subgridIdx).remove(digit);
                isStarterClue[i][j] = false;
                blankCount++;
            }
        }
        return false;
    }

    /**
     * Check if a digit would be valid in a certain cell.
     * <p>
     *     Intended use is to check whether placing a digit in an empty cell is valid.
     *     Mainly used for recursive sudoku generation
     * </p>
     * @param digit the digit to check
     * @param i the row index of the cell
     * @param j the col index of the cell
     * @return whether a digit in a given cell is valid
     */
    boolean validateDigitForEmptyCell(int digit, int i, int j) {
        assert(0 <= i && i < 9 && 0 <= j && j < 9) : "Indices must be in-bounds";
        assert(grid[i][j] == 0) : "Cell must be empty";
        return !generationUsedRowDigits.get(i).contains(digit) && !generationUsedColDigits.get(j).contains(digit)
                && !generationUsedSubgridDigits.get(i / 3 * 3 + j / 3).contains(digit);
    }

    /**
     * Remove a number of cells between {@code MIN_REMOVE} and {@code MAX_REMOVE}
     * <p>
     *     Should only be used in the case that the grid is originally full and everything is already validated,
     *     otherwise unexpected behavior may occur due to the removal of a digit from a set or the removal of
     *     an already empty cell
     * </p>
     * @param removeAmount the number of cells to remove
     */
    void removeRandomCells(int removeAmount) {
        assert(0 <= removeAmount) : "Amount to remove should be positive";
        assert(removeAmount < 81) : "Amount to remove exceeds board size";
        assert(blankCount == 0) : "Perform this action on a full board for generation";
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
            generationUsedRowDigits.get(i).remove(digit);
            generationUsedColDigits.get(j).remove(digit);
            generationUsedSubgridDigits.get(getSubgridIdx(i, j)).remove(digit);

            isStarterClue[i][j] = false;
            blankCount++;
        }
    }
    // ===== END GENERATION METHODS =====


    // ===== MANIPULATION METHODS =====

    /**
     * Update a cell in the grid with a digit
     * @param digit the digit to enter
     * @param i the row index of the cell
     * @param j the col index of the cell
     */
    void enterDigit(int digit, int i, int j) { // todo: add test
        assert(0 <= i && i <= 9 && 0 <= j && j <= 9);
        assert(0 <= digit && digit <= 9);

        if (grid[i][j] == digit || isStarterClue[i][j]) return;

        if (grid[i][j] == 0) blankCount--;
        if (digit == 0) blankCount++;

        grid[i][j] = digit;
    }
    // ===== END MANIPULATION METHODS =====


    // ===== STATIC METHODS =====
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
    // ===== END STATIC METHODS =====
}
