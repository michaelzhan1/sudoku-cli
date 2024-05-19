package org.example.sudoku;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Sudoku game = new Sudoku();
        while (!game.checkFinished()) {
            game.printGrid();
            game.askForDigit();
        }
        game.printGrid();
        System.out.println("You win!");
    }
}
