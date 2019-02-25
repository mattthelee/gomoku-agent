import java.awt.Color;
import java.util.*;


/** The random gomoku player chooses random squares on the board (using a
 *  uniform distribution) until an unoccupied square is found, which is then
 *  returned as the player's move. It is assumed that the board is not full,
 *  otherwise chooseMove() will get stuck in an infinite loop.
 *	Author: Simon Dixon
 **/

// TODO: Need to have the agent stop the search befroe running out of time
    // Do i need to do a reordering if the agent is searching all possible moves? Or do i need to reduce the branching factor? - reducing branching factor was bad but could stil be something i can do here.
    // Need to search deeper, so need to refactor the code for performance
        // Need to check that the win checker is only doing the required run checks
        // Need to check whether it's easier to do a win check by purely checking if a given move wins rather than a given board
            // i.e use the move heuristic rather than the state heuristic, which may make things a lot faster.
    // May want a heuristic based on monte carlo runs - random players were quite fast
    // Not sure that the state heuristic added anything
    // Need to do check for alpha == beta and finish search when that happens - doesn't seem to happen may need to work out why not
    // Do an undo on the board rather than doing a clone - set a

    //Done
    // Need To implement alpha-beta pruning - done

class MiniMaxPlayer180789269 extends GomokuPlayer {
    int maxBranching = 64;
    int maxDepth = 3;
    Color me;
    Color notMe;
    HashMap<String,BoardAnalysis180789269> analysedBoards = new HashMap<String,BoardAnalysis180789269>();

    class BoardAnalysis180789269 {
        List<Move> legalMoves;
        Color winner;
        int[] whiteRuns;
        int[] blackRuns;
        float valueToBlack;
        float valueToWhite;
        int longestWhiteRun;
        int longestBlackRun;
        String boardID;
        Color[][] board;

        BoardAnalysis180789269 (List<Move> legalMoves, Color winner,int longestWhiteRun, int longestBlackRun, String boardID, Color[][] board){
            this.legalMoves = legalMoves;
            this.winner = winner;
            this.longestWhiteRun = longestWhiteRun;
            this.longestBlackRun = longestBlackRun;
            this.boardID = boardID;
            this.board = board;
        }
    }

    public Move chooseMove(Color[][] board, Color me) {
        Move bestMove = new Move(1, 1);
        try {
            long startTime = System.currentTimeMillis();
            //System.out.println("pre minimax " + board[bestMove.row][bestMove.col]);
            this.me = me;
            this.notMe = (me == Color.white) ? Color.black : Color.white;
            // If I don't have a best move so far then take a random legal move
            BoardAnalysis180789269 bd = boardAnalyser(board);
            bd.legalMoves = reorderMovesByHeuristic(board, this.me , bd.legalMoves);
            System.out.println("Number of Legal moves: " + bd.legalMoves.size());
            System.out.println("Whiteruns " + bd.longestWhiteRun);

            if ( bd.legalMoves.size() ==64){
                return new Move(4,4);
            }

            //while (System.currentTimeMillis() < startTime + 9800) { }
            bestMove = alphaBetaSearch(board,this.me, bd);
            //bestMove = bd.legalMoves.get(0);
            Color[][] cloneBoard = deepCloneBoard(board);

            cloneBoard[bestMove.row][bestMove.col] = this.me;

            BoardAnalysis180789269 bd2= boardAnalyser(cloneBoard);
            BoardAnalysis180789269 bd3= fasterBoardAnalyser(bd.boardID, bestMove, me);
            if ((bd2.boardID != bd3.boardID) || (bd2.longestWhiteRun != bd3.longestWhiteRun) || (bd2.longestBlackRun != bd3.longestBlackRun) || (bd2.board != bd3.board)){

                System.out.println("ID1: " + bd2.boardID);
                System.out.println("ID2: " + bd3.boardID);

                System.out.println("Whiterun: " + bd2.longestWhiteRun + ":" + bd3.longestWhiteRun);
                System.out.println("blackrun: " + bd2.longestBlackRun+  ":" + bd3.longestBlackRun);
                System.out.println("board: " + bd2.board + ":" + bd3.board);
            }
            
            //int myMaxRun = getMaxRunForPosition(cloneBoard,this.me, bestMove);
            //System.out.println("Value of this state: " + stateHeuristic(cloneBoard, this.me, this.notMe));
            if (bd2.winner != null)
                System.out.println("My win predictor thinks this will win " + bd2.winner);
            return bestMove;

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            return bestMove; // Might get lucky and have a valid move to return
        }
    }

    private BoardAnalysis180789269 boardAnalyser(Color[][] board){
        // Checks for end of game and returns legalmoves if not ended
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        String boardID = "";
        List<Move> legalMoves = new ArrayList<>();
        BoardAnalysis180789269 bd;

        // Looping through each position is expensive so try to do only once
        for (int row = 0; row < 8; row++ ) {
            for (int col = 0; col < 8; col++) {
                Color token = board[row][col];
                // For each square
                int squareID = row * 8 + col;
                if (token == null) {
                    // For every empty square
                    legalMoves.add(new Move(row, col));
                    boardID = boardID + "0";
                    continue;
                } else if (token == Color.white){
                    boardID = boardID + "w";
                } else {
                    boardID = boardID + "b";
                }
            }
        }

        if (this.analysedBoards.containsKey(boardID)){
            //System.out.println("Loading: " + boardID);
            return this.analysedBoards.get(boardID);
        }

        //System.out.println("*** DEBUG *** ");
        longestWhiteRun = getMaxRunForBoard(board, Color.white);
        if (longestWhiteRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.white, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        longestBlackRun = getMaxRunForBoard(board, Color.black);
        if (longestBlackRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.black, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        bd = new BoardAnalysis180789269(legalMoves, null, longestWhiteRun, longestBlackRun, boardID, board);
        this.analysedBoards.put(boardID,bd);
        return bd;
    }

    private BoardAnalysis180789269 fasterBoardAnalyser(String prevBoardID, Move move, Color moveColor){
        // Checks for end of game and returns legalmoves if not ended
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        String boardID = "";
        List<Move> legalMoves;
        BoardAnalysis180789269 bd;
        BoardAnalysis180789269 prevbd = this.analysedBoards.get(prevBoardID);
        Color[][] board;

        // Looping through each position is expensive so try to do only once
        int moveID = move.row*8 + move.col;
        char changeID = (moveColor == Color.white) ? 'w' : 'b';

        boardID = prevBoardID.substring(0,moveID) + changeID + prevBoardID.substring(moveID + 1);
        if (this.analysedBoards.containsKey(boardID)){
            return this.analysedBoards.get(boardID);
        }

        //Update board
        board = deepCloneBoard(prevbd.board);
        board[move.row][move.col] = moveColor;

        // update runs
        if (moveColor == Color.white) {
            longestWhiteRun = Math.max(getMaxRunForPosition(board, moveColor, move), prevbd.longestWhiteRun);
        } else {
            longestBlackRun = Math.max(getMaxRunForPosition(board, moveColor, move), prevbd.longestBlackRun);
        }
        // Update legalmoves
        legalMoves = new ArrayList<>(prevbd.legalMoves);
        legalMoves.remove(move);

        //System.out.println("*** DEBUG *** ");
        if (longestWhiteRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.white, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        if (longestBlackRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.black, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        bd = new BoardAnalysis180789269(legalMoves, null, longestWhiteRun, longestBlackRun, boardID, board);
        this.analysedBoards.put(boardID,bd);
        return bd;
    }

    private int getMaxRunForBoard(Color[][] board, Color player){
        // iterate down the first row and along the first column
        // this will check all possible rows for runs
        // TODO cn make this more efficient by avoind repeating vert and horizontal checks
        int maxRun = 0;

        maxRun = Math.max(maxRun, getMaxRunForPosition(board, player, new Move(0,0)));
        for (int col = 1; col < 8; col++ ) {
            maxRun = Math.max(maxRun, getMaxRunForPosition(board, player, new Move(0,col)));
        }
        for (int row = 1; row < 8; row++ ) {
            maxRun = Math.max(maxRun, getMaxRunForPosition(board, player, new Move(row,0)));
        }
        return  maxRun;
        }

    private int getMaxRunForPosition(Color[][] board, Color player, Move move){
        // Gives the maximum run for given player, searching only positions connected to given move
        int max1 = Math.max(genHorizontalMaxRun(board,player,move),genVerticalMaxRun(board,player,move));
        int max2 = Math.max(genTLBRDiagMaxRun(board,player,move),genBLTRDiagMaxRun(board,player,move));

        return Math.max(max1,max2);
    }

    private int maxRunFromSeq(Color[][] board, Color player, List<Move> movSeq){
        int maxRun = 0;
        int currentRun = 0;
        for (Move move : movSeq){
            if (board[move.row][move.col] != player){
                currentRun = 0;
                continue;
            }
            currentRun++;
            maxRun = Math.max(maxRun,currentRun);
        }
        return maxRun;
    }

    private int genHorizontalMaxRun(Color[][] board, Color player, Move move){
        // Generate horizontal pos
        List<Move> movSeq = new ArrayList<>();
        for (int col = 0; col < 8; col++ ){
            movSeq.add( new Move(move.row,col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }

    private int genVerticalMaxRun(Color[][] board, Color player, Move move){
        // Generate horizontal pos
        List<Move> movSeq = new ArrayList<>();
        for (int row = 0; row < 8; row++ ){
            movSeq.add( new Move(row,move.col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }

    private int genTLBRDiagMaxRun(Color[][] board, Color player, Move move){
        // Generate horizontal pos
        List<Move> movSeq = new ArrayList<>();
        int yintercept = move.col - move.row;
        int startingCol = Math.max(0,-yintercept);
        int row;

        for (int col = startingCol; col < 8 && (col + yintercept) < 8; col++ ){
            row = col + yintercept;
            movSeq.add( new Move(row,col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }

    private int genBLTRDiagMaxRun(Color[][] board, Color player, Move move){
        // Generate horizontal pos
        List<Move> movSeq = new ArrayList<>();
        int yintercept =  move.col + move.row;
        int startingCol;
        if (yintercept >= 7){
            startingCol = yintercept - 7;
        } else {
            startingCol = 0;
        }
        int row;

        for (int col = startingCol; col < 8 && (-col + yintercept) >= 0 ; col++ ){
            row = -col + yintercept;
            movSeq.add( new Move(row,col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }

    private float stateHeuristic(Color[][] board, Color player, Color nextMove){
        // Returns a measure of the value of a board state to the player
        BoardAnalysis180789269 bd = boardAnalyser(board);
        // If the player has won in this scenario then we want it to have the largest value
        if (bd.winner == player){
            return 2;
        }
        // Gives advantage to those that are playing next
        double initiative = (nextMove == Color.white) ? 0.5 : -0.5;
        // Want a run of 5 to be extemely valuable and a run of 4 to be greatly more valuable than a 3
        double whiteScore = 1/(6.01-(bd.longestWhiteRun + initiative));
        double blackScore = 1/(6.01-(bd.longestBlackRun - initiative));

        float value = (float) (whiteScore - blackScore) / (float) (whiteScore + blackScore);
        bd.valueToWhite = value;
        bd.valueToBlack = -value;
        this.analysedBoards.put(bd.boardID,bd);

        if (player == Color.black){
            value = -value;
        }
        //System.out.println("Value: " + value + " whitescore: " + whiteScore + " blackscore: " + blackScore);
        //System.out.println("Value: " + value + " board: " + Arrays.toString(board) + me);
        return value;
    }

    private float moveHeuristic(Color[][] board, Move move, Color player){
        Color[][] cloneBoard = deepCloneBoard(board);
        cloneBoard[move.row][move.col] = player;
        Color nextPLayer = (player == Color.white) ? Color.black : Color.white;

        // returns a state value between -1 and 1. -1 indicates i lose
        float value = stateHeuristic(cloneBoard, player, nextPLayer);
        return value;
    }

    private List<Move> reorderMovesByHeuristic(Color[][] board, Color me, List<Move> legalMoves){
        CompareMoves180789269 comparator = new CompareMoves180789269(board, me);
        Collections.sort(legalMoves,comparator);
        return legalMoves;
    }

    class CompareMoves180789269 implements Comparator<Move>{
        Color[][] board;
        Color me;
        CompareMoves180789269(Color[][] board, Color me){
            this.board = board;
            this.me = me;
        }

        public int compare(Move a, Move b){
            // Sorts in descending order, i.e highest val will be first now
            float comp = moveHeuristic(this.board, b, this.me) - moveHeuristic(this.board, a, this.me);
            if (comp > 0){
                return 1;
            } else if (comp < 0){
                return -1;
            } else{
                return 0;
            }
        }
    }

    private Move alphaBetaSearch(Color[][] board, Color me, BoardAnalysis180789269 bd){
        Move bestMove = bd.legalMoves.get(0);
        float bestVal = -2;
        Color minColor = (me == Color.white) ? Color.black : Color.white;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("***Trying different top level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            float minVal = minABValue(bd.boardID, legalMove, me, minColor, -2, 2, this.maxDepth);
            //System.out.println("***Best opposition move value " + minVal + " against: " + legalMove.row + ":" + legalMove.col);
            if ( minVal > bestVal){
                bestVal = minVal;
                bestMove = legalMove;
            }
        }
        //System.out.println("***Best move value " + bestVal + " against: " + bestMove.row + ":" + bestMove.col);
        return  bestMove;
    }

    private float maxABValue(String boardID, Move lastMove, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining){
        float value = -2;
        BoardAnalysis180789269 bd = fasterBoardAnalyser(boardID,lastMove,minColor);
        if (bd.winner != null) {
            //System.out.println("Got to a max win");
            return value;
        }
        --depthRemaining;
        if (depthRemaining < 1){
            //System.out.println("Got to maxdepth");
            return stateHeuristic(bd.board, maxColor, maxColor);
        }
        bd.legalMoves = reorderMovesByHeuristic(bd.board, maxColor , bd.legalMoves);
        List<Integer> values = new ArrayList<Integer>();;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("Trying different max level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            value = Math.max(value,minABValue(bd.boardID, legalMove, maxColor, minColor, alpha, beta, depthRemaining));
            if (value >= beta){
                //System.out.println("val>beta");
                return value;
            }
            alpha = Math.max(alpha,value);
        }
        //System.out.println("Max best val");
        return value;
    }


    private float minABValue(String boardID, Move lastMove, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining){
        float value = 2;
        BoardAnalysis180789269 bd = fasterBoardAnalyser(boardID,lastMove,maxColor);
        if (bd.winner != null) {
            //System.out.println("Got to a min win");
            return value; // stand in for infinity
        }
        --depthRemaining;
        if (depthRemaining < 1){
            //System.out.println("Got to max depth");
            return stateHeuristic(bd.board, maxColor, minColor);
        }
        bd.legalMoves = reorderMovesByHeuristic(bd.board, minColor , bd.legalMoves);
        List<Integer> values = new ArrayList<Integer>();;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("Trying different min level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            value = Math.min(beta,maxABValue(bd.boardID, legalMove, maxColor, minColor, alpha, beta, depthRemaining));
            if (value <= alpha){
                //System.out.println("val<alpha");
                return value;
            }
            beta = Math.min(beta,value);
        }
        //xSystem.out.println("Min best val");
        return value;
    }

    private Color[][] deepCloneBoard(Color[][] board){
        Color[][] cloneBoard = new Color[8][8];
        for (int col = 0; col < 8; col++ ) {
            for (int row = 0; row < 8; row++) {
                cloneBoard[row][col] = board[row][col];
            }
        }
        return cloneBoard;
    }
}
