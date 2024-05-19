package org.example.sudoku;

public class App {
    public static void main(String[] args) {
        Sudoku game = new Sudoku();
        while (!game.checkFinished()) {
            // clear output
//            System.out.print("\033[H\033[2J");
//            System.out.flush();

            game.printGrid();
            game.askForDigit();
        }
        game.printGrid();
        System.out.println("You win!");
    }
}
