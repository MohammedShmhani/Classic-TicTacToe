import java.util.Scanner;
import java.util.Random;

public class ClassicTicTacToe {

    static char playerSymbol;
    static char computerSymbol;
    static String difficulty;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);


        /**********************************************************************
         *                        Classic Tic Tac Toe                         *
         **********************************************************************/


        // ================= SYMBOL SELECTION =================
        try {
            System.out.println("=========================================");
            System.out.println("         TIC TAC TOE SYMBOL SETUP");
            System.out.println("=========================================");
            System.out.println("You can choose one letter to represent you in the game.");
            System.out.println("If you choose 'X' or 'O', the computer will take the other one.");
            System.out.println("If you choose any other letter, the computer will be 'O'.");
            System.out.println("Example: X, O, A, B, etc.");
            System.out.println("-----------------------------------------");
            System.out.print("Please enter your symbol: ");

            String symbolInput = input.next().toUpperCase();

            if (symbolInput.equals("X") || symbolInput.equals("O")) {
                playerSymbol = symbolInput.charAt(0);
                computerSymbol = (playerSymbol == 'X') ? 'O' : 'X';
            } else if (symbolInput.length() == 1) {
                playerSymbol = symbolInput.charAt(0);
                computerSymbol = 'O';
                System.out.println("You are using '" + playerSymbol + "' as your symbol. The computer will use 'O'.");
            } else {
                playerSymbol = 'X';
                computerSymbol = 'O';
                System.out.println("Invalid input. Defaulting to X for player and O for computer.");
            }

            // ================= DIFFICULTY SELECTION =================
            while (true) {
                System.out.println("=========================================");
                System.out.println("            SELECT DIFFICULTY");
                System.out.println("=========================================");
                System.out.println("[ 1 ] Easy     - Computer plays randomly");
                System.out.println("[ 2 ] Hard     - Computer plays intelligently (Minimax)");
                System.out.println("-----------------------------------------");
                System.out.print("Please enter your choice: ");

                String difficultyInput = input.next();

                if (difficultyInput.equals("1")) {
                    difficulty = "easy";
                    break;
                } else if (difficultyInput.equals("2")) {
                    difficulty = "hard";
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            }

            // ================= MAIN MENU =================
            int choice;

            while (true) {
                System.out.println("+------------------------------------------------+");
                System.out.println("|            WELCOME TO TIC TAC TOE              |");
                System.out.println("+------------------------------------------------+");
                System.out.println("            1. Play Single Round");
                System.out.println("            2. Play Best of 3");
                System.out.println("            3. Exit");
                System.out.println("--------------------------------------------------");
                System.out.print("            Enter your choice: ");

                try {
                    if (input.hasNextInt()) {
                        choice = input.nextInt();

                        if (choice == 1) {
                            System.out.println("************ SINGLE ROUND ************");
                            char result = playRound();
                            System.out.println("************ FINAL RESULT ************");
                            printResult(result);
                            System.out.println("Returning to main menu...");
                        } else if (choice == 2) {
                            playBestOf3();
                        } else if (choice == 3) {
                            System.out.println("Thanks for playing! Goodbye!");
                            break;
                        } else {
                            System.out.println("Invalid input, please try again.");
                        }
                    } else {
                        throw new Exception("Non-numeric input entered.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input, please enter a number.");
                    input.next(); // clear invalid input
                }
            }

        } catch (Exception e) {
            System.out.println("An error occurred while setting up the game. Please restart.");
        }

    }


    // ======================== Methods Area ================================


    // This method determines the computer's move based on the selected difficulty
    static void computerBestMove(char[][] board) {
        try {
            Random random = new Random();

            // Easy mode: Computer picks a random available cell
            if (difficulty.equals("easy")) {
                int row, col;
                do {
                    row = random.nextInt(3); // Random row (0-2)
                    col = random.nextInt(3); // Random column (0-2)
                } while (board[row][col] == playerSymbol || board[row][col] == computerSymbol);

                board[row][col] = computerSymbol;

            } else {
                // Hard mode: Computer uses Minimax to find the best move
                int bestScore = Integer.MIN_VALUE;
                int moveRow = -1, moveCol = -1;

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        // Check if the cell is available
                        if (board[i][j] != playerSymbol && board[i][j] != computerSymbol) {

                            char backup = board[i][j]; // Save the original value
                            board[i][j] = computerSymbol; // Try computer's move

                            int score = minimax(board, false); // Evaluate the move
                            board[i][j] = backup; // Restore the original value

                            // Update best move if the score is better
                            if (score > bestScore) {
                                bestScore = score;
                                moveRow = i;
                                moveCol = j;
                            }
                        }
                    }
                }

                // Make the best move found
                board[moveRow][moveCol] = computerSymbol;
            }

        } catch (Exception e) {
            // Catch any unexpected errors during the computer's move
            System.out.println("An error occurred while choosing the computer's move: " + e.getMessage());
        }
    }

    // This method plays a single round of the game (player vs. computer)
    static char playRound() {
        Scanner input = new Scanner(System.in);
        char[][] board = initializeBoard(); // Initialize the game board with numbers 1-9

        while (true) {
            printBoard(board); // Display the current state of the board
            System.out.println("Your Turn (" + playerSymbol + "):");

            char choice;

            // Player's move
            while (true) {
                try {
                    System.out.println("Enter the number you want to replace with " + playerSymbol + ":");
                    String userInput = input.next(); // Get user input as a String

                    // Check if the input is a single character and between 1-9
                    if (userInput.length() != 1 || userInput.charAt(0) < '1' || userInput.charAt(0) > '9') {
                        System.out.println("Invalid input. Please enter a single number between 1 and 9.");
                        continue;
                    }

                    choice = userInput.charAt(0);

                    // Check if the chosen position is available
                    if (!replaceChoice(board, choice, playerSymbol)) {
                        System.out.println("This position is already taken. Try another one.");
                        continue;
                    }

                    break; // Valid input, exit the player's move loop

                } catch (Exception e) {
                    // Handle any unexpected errors during player input
                    System.out.println("An error occurred while reading your input. Please try again.");
                    input.nextLine(); // Clear invalid input from the buffer
                }
            }

            // Check if the player won or it's a draw
            char result = checkWinner(board);
            if (result == playerSymbol || result == computerSymbol || result == 'D') {
                printBoard(board); // Show the final board before returning the result
                return result;
            }

            // Computer's move
            System.out.println("Computer's Turn (" + computerSymbol + "):");
            try {
                computerBestMove(board); // Computer decides and makes a move
            } catch (Exception e) {
                System.out.println("Error during computer's turn: " + e.getMessage());
            }

            // Print the board after computer's move
            printBoard(board);

            // Check if the computer won or it's a draw
            result = checkWinner(board);
            if (result == playerSymbol || result == computerSymbol || result == 'D') return result;
        }
    }

    // This method plays a Best of 3 match and keeps track of the score
    static void playBestOf3() {
        int playerWins = 0;
        int computerWins = 0;

        for (int round = 1; round <= 3; round++) {
            System.out.println("************ ROUND " + round + " ************");

            try {
                // Play a single round and get the winner
                char winner = playRound();

                // Update the score based on the winner
                if (winner == playerSymbol) playerWins++;
                else if (winner == computerSymbol) computerWins++;

                // Display end-of-round summary
                System.out.println("************ END OF ROUND " + round + " ************\n");
                System.out.println("   >> Current Score <<");
                System.out.println("   Player   : " + playerWins);
                System.out.println("   Computer : " + computerWins + "\n");

                // If one of them wins 2 rounds → End the match early
                if (playerWins == 2 || computerWins == 2) {
                    System.out.println("\n************ FINAL SCORE ************\n");
                    System.out.println("Player: " + playerWins + " | Computer: " + computerWins + "\n");

                    if (playerWins > computerWins)
                        System.out.println("************ YOU WIN THE MATCH! ************\n");
                    else
                        System.out.println("************ COMPUTER WINS THE MATCH! ************\n");

                    System.out.println("Returning to main menu...\n");
                    break; // End the Best of 3
                }

            } catch (Exception e) {
                // Catch unexpected errors in the round
                System.out.println("An error occurred in Round " + round + ": " + e.getMessage());
                break; // Exit Best of 3 in case of error
            }
        }

        // Final score summary (in case all 3 rounds were played)
        System.out.println("************ FINAL SCORE ************");
        System.out.println("Player: " + playerWins + " | Computer: " + computerWins);

        if (playerWins > computerWins)
            System.out.println("************ YOU WIN THE MATCH! ************");
        else if (computerWins > playerWins)
            System.out.println("************ COMPUTER WINS THE MATCH! ************");
        else
            System.out.println("************ IT'S A DRAW! ************");

        System.out.println("Returning to main menu...");
    }

    // This method initializes the game board with numbers from '1' to '9'
// Each cell in the 3x3 board will hold a character representing its position
    static char[][] initializeBoard() {
        char[][] board = new char[3][3];  // Create a 3x3 char array
        char number = '1';                // Start filling with character '1'

        // Fill the board row by row with numbers 1 to 9
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = number++;

        return board; // Return the initialized board
    }

    // This method prints the current state of the game board in a formatted layout
    static void printBoard(char[][] board) {
        System.out.println();
        System.out.println("     1     2     3");  // Column headers
        System.out.println("  +-----+-----+-----+");

        // Loop through each row and column to display the board
        for (int i = 0; i < 3; i++) {
            System.out.print((i + 1) + " |  ");  // Row header (1 to 3)
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j]);   // Print the cell content
                if (j < 2) System.out.print("  |  ");  // Vertical dividers between cells
            }
            System.out.println("  |");
            System.out.println("  +-----+-----+-----+"); // Horizontal divider after each row
        }

        System.out.println();
    }

    // This method replaces a cell value on the board with the given symbol if the cell matches the user's choice
// Returns true if the replacement was successful, false otherwise
    static boolean replaceChoice(char[][] board, char choice, char symbol) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == choice) { // Check if the current cell matches the user's input
                    board[i][j] = symbol;    // Replace the cell with the player's or computer's symbol
                    return true;             // Replacement successful
                }
        return false; // No matching cell found
    }

    // This method checks the board for a winner or a draw
// Returns the symbol of the winner ('X', 'O', or other), 'D' for draw, or ' ' if the game is still ongoing
    static char checkWinner(char[][] board) {

        // Check rows and columns for 3 in a row
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0]; // Row match
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i]; // Column match
        }

        // Check both diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0]; // Main diagonal
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2]; // Anti-diagonal

        // Check if there are still empty cells → game is not finished
        for (char[] row : board)
            for (char cell : row)
                if (cell != playerSymbol && cell != computerSymbol)
                    return ' '; // Game is still ongoing

        return 'D'; // If no winner and no empty cells → it's a draw
    }

    // This method implements the Minimax algorithm to calculate the best possible move for the computer
// It recursively evaluates all possible future moves
    static int minimax(char[][] board, boolean isMaximizing) {

        // First, check if there is a winner or if it's a draw
        char winner = checkWinner(board);
        if (winner == computerSymbol) return 10;    // Computer wins → high score
        if (winner == playerSymbol) return -10;     // Player wins → low score
        if (winner == 'D') return 0;                // Draw → neutral score

        // Initialize the best score depending on whether we are maximizing or minimizing
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Loop over all cells to try each possible move
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // If the cell is empty
                if (board[i][j] != playerSymbol && board[i][j] != computerSymbol) {

                    char backup = board[i][j];  // Backup the current cell value

                    // Try the move for the current player (computer if maximizing, player if minimizing)
                    board[i][j] = isMaximizing ? computerSymbol : playerSymbol;

                    // Recursively call minimax to evaluate the move
                    int score = minimax(board, !isMaximizing);

                    // Restore the original value
                    board[i][j] = backup;

                    // Update the best score depending on whose turn it is
                    if (isMaximizing)
                        bestScore = Math.max(score, bestScore);
                    else
                        bestScore = Math.min(score, bestScore);
                }
            }
        }

        return bestScore;  // Return the best score found
    }

    // This method prints the result of the round based on the winner symbol
    static void printResult(char result) {
        // Check who won the round and display the appropriate message
        if (result == playerSymbol) {
            System.out.println("Player wins the round!");
        } else if (result == computerSymbol) {
            System.out.println("Computer wins the round!");
        } else {
            System.out.println("It's a draw!");
        }
    }
}
