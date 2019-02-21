import java.awt.Color;
import java.util.*;

/** The random gomoku player chooses random squares on the board (using a
 *  uniform distribution) until an unoccupied square is found, which is then
 *  returned as the player's move. It is assumed that the board is not full,
 *  otherwise chooseMove() will get stuck in an infinite loop.
 *	Author: Simon Dixon
 **/
class MiniMaxPlayer180789269 extends GomokuPlayer {
    int[][] allRuns = new int[][]{{0, 32, 64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 36, 65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 40, 66, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 3, 44, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 3, 48, 80, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {1, 2, 3, 52, 81, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {2, 3, 56, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {3, 60, 83, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 32, 33, 68, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 36, 37, 64, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 40, 41, 65, 70, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 7, 44, 45, 66, 71, 80, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 7, 48, 49, 67, 81, 84, -1, -1, -1, -1, -1, -1, -1}, {5, 6, 7, 52, 53, 82, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {6, 7, 56, 57, 83, 86, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {7, 60, 61, 87, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 32, 33, 34, 72, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 9, 36, 37, 38, 68, 73, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 9, 10, 40, 41, 42, 64, 69, 74, 80, -1, -1, -1, -1, -1, -1}, {8, 9, 10, 11, 44, 45, 46, 65, 70, 75, 81, 84, -1, -1, -1, -1}, {8, 9, 10, 11, 48, 49, 50, 66, 71, 82, 85, 88, -1, -1, -1, -1}, {9, 10, 11, 52, 53, 54, 67, 83, 86, 89, -1, -1, -1, -1, -1, -1}, {10, 11, 56, 57, 58, 87, 90, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {11, 60, 61, 62, 91, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {12, 32, 33, 34, 35, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {12, 13, 36, 37, 38, 39, 72, 77, 80, -1, -1, -1, -1, -1, -1, -1}, {12, 13, 14, 40, 41, 42, 43, 68, 73, 78, 81, 84, -1, -1, -1, -1}, {12, 13, 14, 15, 44, 45, 46, 47, 64, 69, 74, 79, 82, 85, 88, -1}, {12, 13, 14, 15, 48, 49, 50, 51, 65, 70, 75, 83, 86, 89, 92, -1}, {13, 14, 15, 52, 53, 54, 55, 66, 71, 87, 90, 93, -1, -1, -1, -1}, {14, 15, 56, 57, 58, 59, 67, 91, 94, -1, -1, -1, -1, -1, -1, -1}, {15, 60, 61, 62, 63, 95, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {16, 32, 33, 34, 35, 80, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {16, 17, 36, 37, 38, 39, 76, 81, 84, -1, -1, -1, -1, -1, -1, -1}, {16, 17, 18, 40, 41, 42, 43, 72, 77, 82, 85, 88, -1, -1, -1, -1}, {16, 17, 18, 19, 44, 45, 46, 47, 68, 73, 78, 83, 86, 89, 92, -1}, {16, 17, 18, 19, 48, 49, 50, 51, 64, 69, 74, 79, 87, 90, 93, -1}, {17, 18, 19, 52, 53, 54, 55, 65, 70, 75, 91, 94, -1, -1, -1, -1}, {18, 19, 56, 57, 58, 59, 66, 71, 95, -1, -1, -1, -1, -1, -1, -1}, {19, 60, 61, 62, 63, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 33, 34, 35, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 21, 37, 38, 39, 85, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 21, 22, 41, 42, 43, 76, 86, 89, 92, -1, -1, -1, -1, -1, -1}, {20, 21, 22, 23, 45, 46, 47, 72, 77, 87, 90, 93, -1, -1, -1, -1}, {20, 21, 22, 23, 49, 50, 51, 68, 73, 78, 91, 94, -1, -1, -1, -1}, {21, 22, 23, 53, 54, 55, 69, 74, 79, 95, -1, -1, -1, -1, -1, -1}, {22, 23, 57, 58, 59, 70, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {23, 61, 62, 63, 71, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 34, 35, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 38, 39, 89, 92, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 42, 43, 90, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 27, 46, 47, 76, 91, 94, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 27, 50, 51, 72, 77, 95, -1, -1, -1, -1, -1, -1, -1}, {25, 26, 27, 54, 55, 73, 78, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {26, 27, 58, 59, 74, 79, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {27, 62, 63, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 35, 92, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 39, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 43, 94, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 31, 47, 95, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 31, 51, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {29, 30, 31, 55, 77, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {30, 31, 59, 78, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {31, 63, 79, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};
    int[] whiteRuns = new int[96];
    int[] blackRuns = new int[96];

    public void init(){
        for(int i = 0; i < 96; ++i) {
            this.whiteRuns[i] = this.blackRuns[i] = 0;
        }
    }

    public Move chooseMove(Color[][] board, Color me) {
        Move bestMove = new Move(1, 1);
        try {
            long startTime = System.currentTimeMillis();

            // If I don't have a best move so far then take a random legal move
            List<Move> legalMoves = getLegalMoves(board);
            System.out.println("Number of Legal moves: " + legalMoves.size());

            //while (System.currentTimeMillis() < startTime + 9800) { }

            bestMove = legalMoves.get((int) Math.random() * legalMoves.size());
            //System.out.println("Failed to find move in time, choosing best so far");
            if (doesMoveWin(board, bestMove))
                System.out.println("My win predictor thinks this will win");
            return bestMove;

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            return bestMove; // Might get lucky and have a valid move to return
        }
    }

    private List<Move>  getLegalMoves(Color[][] board){ // Method returns all current legal moves
        List<Move> legalMoves = new ArrayList<>();
        for (int col = 0; col < 7; col++ ){
            for (int row = 0; row < 7; row++){
                if (board[row][col] == null)
                    legalMoves.add(new Move(row,col));
            }
        }
        return legalMoves;
    }

    //private Move chooseMove(Color[][] board, Color me, List<Move>  legalMoves, long startTime){ }

    boolean doesMoveWin(Color[][] board, Move testMove){
        int row = testMove.row;
        int col = testMove.col;

        for(int runNo = 0; this.allRuns[row * 8 + col][runNo] != -1; ++runNo) {
            if (++this.whiteRuns[this.allRuns[row * 8 + col][runNo]] == 5) {
                return true;
            }
        }
        for(int runNo = 0; this.allRuns[row * 8 + col][runNo] != -1; ++runNo) {
            if (++this.blackRuns[this.allRuns[row * 8 + col][runNo]] == 5) {
                return true;
            }
        }
        return false;
    }
/*
    private int[] minimax(int player, List<Move>  legalMoves, Color[][] board){
        int winner;
        int row = col = -1;
        // Set player int so 1 is white and black is -1
        if (gomokuBoard.winner == Color.white)
            winner = 1;
        else if (gomokuBoard.winner == Color.black)
            winner = -1;


        for (Move legalMove : legalMoves){
            gomokuBoard.makeMove(legalMove, )
            [row,col,value]  = minimax()
        }

    }

    private int convertBoardToInt(Color[][] board){
        int[][] newBoard;
        for (int col = 0; col == 7; col++){
            for (int row = 0; row == 7; row ++){
                newBoard[col][row] = 0
                Color var4 = this.turn == Color.white ? Color.black : Color.white;
            }
        }
    }

    public convolve(Color[][] filter, Color[][] board, Color me){
        int filterWidth = filter.length;
        int filterHeight = filter[0]length;

        for (int col = 0; col == 7-filterWidth ; col ++ ){
            for (int row = 0; row == 7; row ++){
                // output[col][row] = my color if the convolution of the filter and the board is white, if black then is black else blank
            }

        }

    } */
}
