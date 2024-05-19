package me.antcode;

import java.util.Scanner;

public class Game {

    private final Tile[][] grid;

    private final int NUM_ROW_COL;

    private boolean hasMovesLeft;

    private int moves;


    public Game(){
        NUM_ROW_COL = 4;
        grid = new Tile[NUM_ROW_COL][NUM_ROW_COL];
        for (int row = 0; row < NUM_ROW_COL; row ++){
      for (int col = 0; col < NUM_ROW_COL; col++) {
          grid[row][col] = new Tile(0, row, col);
        }
        }
        placeRandomPiece();
        hasMovesLeft = true;
        moves = 0;
        printGrid(true);

        Scanner scanner = new Scanner(System.in);
        while (hasMovesLeft){
            System.out.println("Enter 'left', 'l', 'right', 'r', 'up', 'u', 'down', or 'd':   ('exit' to quit):");
            String move = scanner.nextLine().trim().toLowerCase();
            if (move.equals("exit")){
                executeEndCredits();
                return;
            }
           while (!foundMove(move)){
               if (move.equals("exit")){
                   executeEndCredits();
                   return;
               }
               System.out.println("Invalid move. Enter 'left', 'l', 'right', 'r', 'up', 'u', 'down', or 'd':   ('exit' to quit)");
               move = scanner.nextLine().trim().toLowerCase();
           }
           executeMove(move);
        }
    }

    /**
     * Determines whether the parameterized value is a valid entry for a move.
     * @param move value to check if it is valid
     * @return true if value is a valid move; false otherwise.
     */
    private boolean foundMove(String move) {
        return move.equals("left") || move.equals("l") ||
                move.equals("right") || move.equals("r") ||
                move.equals("up") || move.equals("u") ||
                move.equals("down") || move.equals("d");
    }


    /**
     * Executes the move depending on parameterized value and prints the updated grid.
     * @param move direction to execute move through
     */
    private void executeMove(String move){
        moves++;
        Tile[][] checkGrid = dupeGrid();
        switch (move){
            case "left", "l":
                pushAndCombine("left");
                break;
            case "right", "r":
                pushAndCombine("right");
                break;
            case "up", "u":
                pushAndCombine("up");
                break;
            case "down", "d":
                pushAndCombine("down");
                break;
            case "exit":
                executeEndCredits();
                break;
            default:
                // Handle unexpected move input
                System.out.println("Invalid move. Enter 'left', 'l', 'right', 'r', 'up', 'u', 'down', or 'd':   ('exit' to quit)");
                break; // Add break statement for default case
        }
        printGrid(areArraysEqual(checkGrid, grid));
    }

    private void pushAndCombine(String direction){
        pushZeros(direction);
        combineNumbers(direction, this.grid);
    }


    /**
   * Combines numbers if they are the same value and are accessible between each other
   * Ex(2 | 4 | 4 | 2) -> (2 | 8 |2 | 0)
   * @param direction whether to combine going to the right, or going to the left
   */
  private void combineNumbers(String direction, Tile[][] grid) {
      switch (direction) {
          case "left" -> {
              for (Tile[] row : grid) {
                  for (int i = 0; i < row.length - 1; i++) {
                      int currentSpotValue = row[i].getValue();
                      int neighborSpotValue = row[i + 1].getValue();
                      if (currentSpotValue == neighborSpotValue) {
                          row[i].setValue(currentSpotValue * 2);
                          row[i + 1].setValue(0);
                          pushZeros("left");
                      }
                  }
              }
          }
          case "right" -> {
              for (Tile[] row : grid) {
                  for (int i = row.length - 1; i > 0; i--) {
                      int currentSpotValue = row[i].getValue();
                      int neighborSpotValue = row[i - 1].getValue();
                      if (currentSpotValue == neighborSpotValue) {
                          row[i].setValue(currentSpotValue * 2);
                          row[i - 1].setValue(0);
                          pushZeros("right");
                      }
                  }
              }
          }
          case "up" -> {
              for (int col = 0; col < grid[0].length; col++) {
                  for (int row = 0; row < grid.length - 1; row++) {
                      int currentSpotValue = grid[row][col].getValue();
                      int neighborSpotValue = grid[row + 1][col].getValue();
                      if (currentSpotValue == neighborSpotValue) {
                          grid[row][col].setValue(currentSpotValue * 2);
                          grid[row + 1][col].setValue(0);
                          pushZeros("up");
                      }
                  }
              }
          }
          case "down" -> {
              for (int col = 0; col < grid[0].length; col++) {
                  for (int row = grid.length - 1; row > 0; row--) {
                      int currentSpotValue = grid[row][col].getValue();
                      int neighborSpotValue = grid[row - 1][col].getValue();
                      if (currentSpotValue == neighborSpotValue) {
                          grid[row][col].setValue(currentSpotValue * 2);
                          grid[row - 1][col].setValue(0);
                          pushZeros("down");
                      }
                  }
              }
          }
      }
        }

    /**
     * Checks if there are any combinations left on the grid.
     * @return true if there are combinations left; false otherwise
     */
    private boolean hasCombinationsRemaining() {
        for (int row = 0; row < NUM_ROW_COL; row++) {
            for (int col = 0; col < NUM_ROW_COL; col++) {
                int currentValue = grid[row][col].getValue();

                // Check left
                if (col > 0 && grid[row][col - 1].getValue() == currentValue) {
                    return true;
                }

                // Check right
                if (col < NUM_ROW_COL - 1 && grid[row][col + 1].getValue() == currentValue) {
                    return true;
                }

                // Check up
                if (row > 0 && grid[row - 1][col].getValue() == currentValue) {
                    return true;
                }

                // Check down
                if (row < NUM_ROW_COL - 1 && grid[row + 1][col].getValue() == currentValue) {
                    return true;
                }
            }
        }
        return false; // No combinations found
    }



    /**
     * Pushes the zeroes to the opposite direction, to allow for combination
     * Ex(2 | 0 | 0 | 2) -> (2 | 2 | 0 | 0)
     * @param direction direction to determine which way the zeroes should go
     */
    private void pushZeros(String direction) {
        switch (direction) {
            case "left" -> shiftTilesHorizontal(1);
            case "right" -> shiftTilesHorizontal(-1);
            case "up" -> shiftTilesVertical(1);
            case "down" -> shiftTilesVertical(-1);
        }
    }

    /**
     * Shifts tiles horizontally based on the specified direction.
     * @param direction the direction to shift (-1 for left, 1 for right)
     */
    private void shiftTilesHorizontal(int direction) {
        for (Tile[] rows : grid) {
            for (int i = direction == 1 ? 0 : rows.length - 1; direction == 1 ? i < rows.length - 1 : i > 0; i += direction) {
                if (rows[i].getValue() == 0) {
                    for (int j = i + direction; direction == 1 ? j < rows.length : j >= 0; j += direction) {
                        if (rows[j].getValue() != 0) {
                            rows[i].setValue(rows[j].getValue());
                            rows[j].setValue(0);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shifts tiles vertically based on the specified direction.
     * @param direction the direction to shift (-1 for up, 1 for down)
     */
    private void shiftTilesVertical(int direction) {
        for (int col = 0; col < grid[0].length; col++) {
            for (int row = direction == 1 ? 0 : grid.length - 1; direction == 1 ? row < grid.length - 1 : row > 0; row += direction) {
                if (grid[row][col].getValue() == 0) {
                    for (int k = row + direction; direction == 1 ? k < grid.length : k >= 0; k += direction) {
                        if (grid[k][col].getValue() != 0) {
                            grid[row][col].setValue(grid[k][col].getValue());
                            grid[k][col].setValue(0);
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * Places a number, either two or four, at a random spot on the board.
     */
    private void placeRandomPiece(){
        if (hasNoOpenSpaces(this.grid)) return;
        int randomRow = (int) ((Math.random() * NUM_ROW_COL ));
        int randomCol = (int) ((Math.random() * NUM_ROW_COL ));
        if (grid[randomRow][randomCol].getValue() == 0){
            int num = Math.random() < 0.5 ? 2 : 4;
            grid[randomRow][randomCol].setValue(num);
        }else{
            placeRandomPiece();
        }
    }

    /**
     * Checks to see if the board has any open spaces, meaning a value on the board equals zero.
     * @return true if there is not an open space; false otherwise
     */
    private boolean hasNoOpenSpaces(Tile[][] grid){
        for (Tile[] rows : grid){
            for (Tile tile : rows){
                if (tile.getValue() == 0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prints the current grid
     */
    private void printGrid(boolean shouldPlaceRandom){
    if (shouldPlaceRandom) {
      placeRandomPiece();
        }
        for (int row = 0; row < NUM_ROW_COL; row ++){
      for (int col = 0; col < NUM_ROW_COL; col++) {
        if (col == NUM_ROW_COL - 1) {
          System.out.println(grid[row][col].getValue());
        } else {
          System.out.print(grid[row][col].getValue() + " | ");
        }
                }
        }
        if (!hasCombinationsRemaining()){
            executeEndCredits();
        }

    }

    /**
     * Finds highest value in the current game
     * @return highest value found.
     */
    private int findHighestValue(){
        int max = 0;
        for (Tile[] row : grid){
            for (Tile tile : row){
                if (tile.getValue() > max){
                    max = tile.getValue();
                }
            }
        }
        return max;
    }

    /**
     * Disables the game and displays the player's stats
     */
    private void executeEndCredits(){
        hasMovesLeft = false;
    System.out.println("                [GAME OVER]     ");
    System.out.println("[Highest Count]: " + findHighestValue());
    System.out.println("[Total Moves]:   " + moves);
  }

    /**
     * Duplicates the grid to check if anything had changed as a result of the action
     * @return the grid duplicated
     */
    private Tile[][] dupeGrid(){
        Tile[][] checkGrid = new Tile[NUM_ROW_COL][NUM_ROW_COL];
        for (int row = 0; row < grid.length; row++){
            for (int col = 0; col <grid[row].length; col++){
                checkGrid[row][col] = new Tile(grid[row][col].getValue(), row, col);
            }
        }
        return checkGrid;
    }

    /**
     * Checks to see if two arrays are the same
     * @param array1 Array one to compare to Array two
     * @param array2 Array two to compare to Array One
     * @return true if there was motion; false otherwise
     */
    public static boolean areArraysEqual(Tile[][] array1, Tile[][] array2) {
        if (array1.length != array2.length || array1[0].length != array2[0].length) {
            return false; // Arrays have different dimensions
        }
        for (int i = 0; i < array1.length; i++) {
            for (int j = 0; j < array1[0].length; j++) {
                if (array1[i][j].getValue() != array2[i][j].getValue()) {
                    return true; // Elements at position (i, j) are not equal
                }
            }
        }
        return false; // All elements are equal
    }

}
