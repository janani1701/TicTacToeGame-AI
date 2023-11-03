import java.util.Random;
import java.util.Scanner;

public class TicTacToeGame {
    private static final int SIZE = 3;
    private static char[][] board = new char[SIZE][SIZE];
    private static final char EMPTY = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private static Random random = new Random();

    public static void main(String[] args) {
        initializeBoard();
        printBoard();

        int difficultyLevel = getDifficultyLevelChoice();
        char playerSymbol = getPlayerSymbolChoice();
        boolean gameWon = false;

        while (true) {
            if (playerSymbol == PLAYER_X) {
                getPlayerMove(playerSymbol);
            } else {
                if (difficultyLevel == 1) {
                    makeRandomAIMove(playerSymbol);
                } else if (difficultyLevel == 2) {
                    int[] bestMove = minimax(board, playerSymbol);
                    board[bestMove[0]][bestMove[1]] = playerSymbol;
                } else if (difficultyLevel == 3) {
                    int[] bestMove = alphaBetaPruning(board, playerSymbol, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                    board[bestMove[0]][bestMove[1]] = playerSymbol;
                }
            }

            printBoard();
            gameWon = checkForWin(playerSymbol);
            if (gameWon || isBoardFull()) {
                if (gameWon) {
                    if (playerSymbol == PLAYER_X) {
                        System.out.println("Player X wins!");
                    } else {
                        System.out.println("Player " + (playerSymbol == PLAYER_O ? "O" : "AI") + " wins!");
                    }
                } else {
                    System.out.println("It's a draw!");
                }
                break;
            }

            playerSymbol = (playerSymbol == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
    }

    private static int getDifficultyLevelChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select the AI difficulty level:");
        System.out.println("1. Easy (Random Moves)");
        System.out.println("2. Medium (Basic Minimax)");
        System.out.println("3. Difficult (Advanced AI)");
        System.out.print("Enter the corresponding number: ");
        return scanner.nextInt();
    }

    private static char getPlayerSymbolChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose 'X' or 'O' to play as: ");
        String choice = scanner.next();
        return choice.equalsIgnoreCase("X") ? PLAYER_X : PLAYER_O;
    }

    private static void makeRandomAIMove(char player) {
        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (board[row][col] != EMPTY);
        board[row][col] = player;
    }

    private static void getPlayerMove(char player) {
        Scanner scanner = new Scanner(System.in);
        int row, col;
        do {
            System.out.print("Enter your move (row and column, e.g., 1 2): ");
            row = scanner.nextInt() - 1;
            col = scanner.nextInt() - 1;
        } while (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != EMPTY);
        board[row][col] = player;
    }

    private static void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    private static void printBoard() {
        System.out.println("-------------");
        for (int i = 0; i < SIZE; i++) {
            System.out.print("| ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println("\n-------------");
        }
    }

    private static boolean checkForWin(char player) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true; // Row win
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true; // Column win
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true; // Diagonal win (top-right to bottom-left)
        }
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] minimax(char[][] board, char player) {
        int[] bestMove = new int[]{-1, -1};
        int bestScore = (player == PLAYER_O) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;
                    int score = minimaxHelper(board, 0, false, player);
                    board[i][j] = EMPTY;

                    if ((player == PLAYER_O && score > bestScore) || (player == PLAYER_X && score < bestScore)) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    private static int minimaxHelper(char[][] board, int depth, boolean isMaximizing, char player) {
        char opponent = (player == PLAYER_O) ? PLAYER_X : PLAYER_O;
        int score;

        if (checkForWin(player)) {
            return 1;
        } else if (checkForWin(opponent)) {
            return -1;
        } else if (isBoardFull()) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = player;
                        bestScore = Math.max(bestScore, minimaxHelper(board, depth + 1, !isMaximizing, player));
                        board[i][j] = EMPTY;
                    }
                }
            }
            score = bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = opponent;
                        bestScore = Math.min(bestScore, minimaxHelper(board, depth + 1, !isMaximizing, player));
                        board[i][j] = EMPTY;
                    }
                }
            }
            score = bestScore;
        }
        return score;
    }

    private static int[] alphaBetaPruning(char[][] board, char player, int alpha, int beta, boolean isMaximizing) {
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = player;
                    int score = alphaBetaPruningHelper(board, 0, alpha, beta, !isMaximizing, player);
                    board[i][j] = EMPTY;

                    if (isMaximizing) {
                        if (score > alpha) {
                            alpha = score;
                            bestMove[0] = i;
                            bestMove[1] = j;
                        }
                    } else {
                        if (score < beta) {
                            beta = score;
                            bestMove[0] = i;
                            bestMove[1] = j;
                        }
                    }

                    if (alpha >= beta) {
                        return bestMove;
                    }
                }
            }
        }
        return bestMove;
    }

    private static int alphaBetaPruningHelper(char[][] board, int depth, int alpha, int beta, boolean isMaximizing, char player) {
        char opponent = (player == PLAYER_O) ? PLAYER_X : PLAYER_O;
        int score;

        if (checkForWin(player)) {
            return 1;
        } else if (checkForWin(opponent)) {
            return -1;
        } else if (isBoardFull()) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = player;
                        bestScore = Math.max(bestScore, alphaBetaPruningHelper(board, depth + 1, alpha, beta, !isMaximizing, player));
                        board[i][j] = EMPTY;
                        alpha = Math.max(alpha, bestScore);

                        if (alpha >= beta) {
                            return bestScore;
                        }
                    }
                }
            }
            score = bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = opponent;
                        bestScore = Math.min(bestScore, alphaBetaPruningHelper(board, depth + 1, alpha, beta, !isMaximizing, player));
                        board[i][j] = EMPTY;
                        beta = Math.min(beta, bestScore);

                        if (alpha >= beta) {
                            return bestScore;
                        }
                    }
                }
            }
            score = bestScore;
        }
        return score;
    }
}
