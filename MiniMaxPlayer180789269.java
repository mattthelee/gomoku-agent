import java.awt.Color;
import java.util.*;

/** The random gomoku player chooses random squares on the board (using a
 *  uniform distribution) until an unoccupied square is found, which is then
 *  returned as the player's move. It is assumed that the board is not full,
 *  otherwise chooseMove() will get stuck in an infinite loop.
 *	Author: Simon Dixon
 **/
class MiniMaxPlayer180789269 extends GomokuPlayer {

    public Move chooseMove(Color[][] board, Color me) {
        long startTime = System.currentTimeMillis();
        Move bestMove = new Move(1,1);
        // If I don't have a best move so far then take a random legal move
        List<Move> legalMoves = getLegalMoves(board);
        bestMove = legalMoves.get((int) Math.random() * legalMoves.size());
        while (System.currentTimeMillis() < startTime + 9800){

        }
        System.out.println("Failed to find move in time, choosing best so far");
        if (bestMove != null)
            return bestMove;

        while (true) {
            int row = (int) (Math.random() * 8);	// values are from 0 to 7
            int col = (int) (Math.random() * 8);
            if (board[row][col] == null)			// is the square vacant?
                return new Move(row, col);
        }

    }

    private List<Move>  getLegalMoves(Color[][] board){
        List<Move> legalMoves = new ArrayList<>();
        for (int col = 0; col == 7; col ++ ){
            for (int row = 0; row == 7; row ++){
                if (board[row][col] == null)
                    legalMoves.add(new Move(row,col));
            }
        }
        return legalMoves;
    }


/*
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
