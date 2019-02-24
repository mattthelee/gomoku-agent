import java.awt.Color;
import java.util.*;


/** The random gomoku player chooses random squares on the board (using a
 *  uniform distribution) until an unoccupied square is found, which is then
 *  returned as the player's move. It is assumed that the board is not full,
 *  otherwise chooseMove() will get stuck in an infinite loop.
 *	Author: Simon Dixon
 **/

// TODO: Need to have the agent stop the search befroe running out of time
    // Need a reordering function that reorders the legalmoves depending on the heuristic
        // reordering is now working but the alphabetasearch is still not realisin git needs to defend so isn't working properly
        // Can see from the debug statements that the values are coming out thw wrong way for the alphasbetasearch
        // minvalue is finding the min value for better moves to be lower than that for worse moves
    // Save the boards to memory with the board analysis object and heuristic so don't have to keep running it
        // to do this could use a unique number format for an id for the board to do this
    // Not sure that the state heuristic added anything

    //Done
    // Need To implement alpha-beta pruning - done

class MiniMaxPlayer180789269 extends GomokuPlayer {
    int maxBranching = 64;
    int maxDepth = 2;
    Color me;
    Color notMe;
    HashMap<String,BoardAnalysis> analysedBoards = new HashMap<String,BoardAnalysis>();


    class BoardAnalysis {
        List<Move> legalMoves;
        Color winner;
        int[] whiteRuns;
        int[] blackRuns;
        float valueToBlack;
        float valueToWhite;
        int longestWhiteRun;
        int longestBlackRun;
        String boardID;


        BoardAnalysis (List<Move> legalMoves, Color winner, int[] whiteRuns, int[] blackRuns, int longestWhiteRun, int longestBlackRun, String boardID){
            this.whiteRuns = whiteRuns;
            this.blackRuns = blackRuns;
            this.legalMoves = legalMoves;
            this.winner = winner;
            this.longestWhiteRun = longestWhiteRun;
            this.longestBlackRun = longestBlackRun;
            this.boardID = boardID;
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
            BoardAnalysis bd = boardAnalyser(board);
            bd.legalMoves = reorderMovesByHeuristic(board, this.me , bd.legalMoves);
            System.out.println("Number of Legal moves: " + bd.legalMoves.size());

            if ( bd.legalMoves.size() ==64){
                return new Move(4,4);

            }

            //while (System.currentTimeMillis() < startTime + 9800) { }
            bestMove = alphaBetaSearch(board,this.me, bd);
            //bestMove = bd.legalMoves.get(0);
            Color[][] cloneBoard = deepCloneBoard(board);

            cloneBoard[bestMove.row][bestMove.col] = this.me;

            BoardAnalysis bd2= boardAnalyser(cloneBoard);
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


    private BoardAnalysis boardAnalyser(Color[][] board){
        // Checks for end of game and returns legalmoves if not ended
        int[] whiteRuns = new int[96];
        int[] blackRuns = new int[96];
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        String boardID = "";
        List<Move> legalMoves = new ArrayList<>();
        BoardAnalysis bd;

        // Looping through each position is expensive so try to do only once
        for (int col = 0; col < 8; col++ ) {
            for (int row = 0; row < 8; row++) {
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
            return analysedBoards.get(boardID);
        }

        //System.out.println("*** DEBUG *** ");
        longestWhiteRun = getMaxRunForBoard(board, Color.white);
        if (longestWhiteRun >= 5) {
            bd = new BoardAnalysis(legalMoves, Color.white, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun, boardID);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        longestBlackRun = getMaxRunForBoard(board, Color.black);
        if (longestBlackRun >= 5) {
            bd = new BoardAnalysis(legalMoves, Color.black, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun, boardID);
            this.analysedBoards.put(boardID,bd);
            return bd;
        }
        bd = new BoardAnalysis(legalMoves, null, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun, boardID);
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

        for (int col = startingCol; col < 8 && col + yintercept < 8; col++ ){
            row = col + yintercept;
            movSeq.add( new Move(row,col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }

    private int genBLTRDiagMaxRun(Color[][] board, Color player, Move move){
        // Generate horizontal pos
        List<Move> movSeq = new ArrayList<>();
        int yintercept =  move.col + move.row;
        int startingCol = Math.min(8,yintercept);
        int row;

        for (int col = startingCol; col < 8 && -col + yintercept > 0; col++ ){
            row = -col + yintercept;
            movSeq.add( new Move(row,col));
        }
        return  maxRunFromSeq(board, player,movSeq);
    }


    private float stateHeuristic(Color[][] board, Color player, Color nextMove){
        BoardAnalysis bd = boardAnalyser(board);

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
        CompareMoves comparator = new CompareMoves(board, me);
        Collections.sort(legalMoves,comparator);
        return legalMoves;
    }

    class CompareMoves implements Comparator<Move>{
        Color[][] board;
        Color me;

        CompareMoves(Color[][] board, Color me){
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

    private Move alphaBetaSearch(Color[][] board, Color me, BoardAnalysis bd){
        Move bestMove = bd.legalMoves.get(0);
        float bestVal = -2;
        Color minColor = (me == Color.white) ? Color.black : Color.white;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("***Trying different top level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = me;
            float minVal = minABValue(cloneBoard, me, minColor, -2, 2, this.maxDepth);
            //System.out.println("***Best opposition move value " + minVal + " against: " + legalMove.row + ":" + legalMove.col);
            if ( minVal > bestVal){
                bestVal = minVal;
                bestMove = legalMove;
            }
        }
        //System.out.println("***Best move value " + bestVal + " against: " + bestMove.row + ":" + bestMove.col);
        return  bestMove;
    }

    private float maxABValue(Color[][] board, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining){
        float value = -2;
        BoardAnalysis bd = boardAnalyser(board);
        if (bd.winner != null) {
            //System.out.println("Got to a max win");

            return -1;
        }
        --depthRemaining;
        if (depthRemaining < 1){
            //System.out.println("Got to maxdepth");
            return stateHeuristic(board, maxColor, maxColor);
        }
        bd.legalMoves = reorderMovesByHeuristic(board, maxColor , bd.legalMoves);
        List<Integer> values = new ArrayList<Integer>();;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("Trying different max level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = maxColor;
            value = Math.max(value,minABValue(cloneBoard, maxColor, minColor, alpha, beta, depthRemaining));
            if (value >= beta){
                //System.out.println("val>beta");
                return value;
            }
            alpha = Math.max(alpha,value);

        }
        //System.out.println("Max best val");

        return value;
    }

    private float minABValue(Color[][] board, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining){
        float value = 2;
        BoardAnalysis bd = boardAnalyser(board);
        if (bd.winner != null) {
            //System.out.println("Got to a min win");
            return 1; // stand in for infinity
        }
        --depthRemaining;
        if (depthRemaining < 1){
            //System.out.println("Got to max depth");
            return stateHeuristic(board, maxColor, maxColor);
        }
        bd.legalMoves = reorderMovesByHeuristic(board, minColor , bd.legalMoves);
        List<Integer> values = new ArrayList<Integer>();;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("Trying different min level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = minColor;
            value = Math.min(beta,maxABValue(cloneBoard, maxColor, minColor, alpha, beta, depthRemaining));
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
