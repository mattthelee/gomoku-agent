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
    // Something is wrong with the max run calculation
    // Save the boards to memory with the board analysis object and heuristic so don't have to keep running it
        // to do this could use a unique number format for an id for the board to do this
    // Not sure that the state heuristic added anything

    //Done
    // Need To implement alpha-beta pruning - done

class MiniMaxPlayer180789269 extends GomokuPlayer {
    int[][] allRuns = new int[][]{{0, 32, 64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 36, 65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 40, 66, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 3, 44, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {0, 1, 2, 3, 48, 80, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {1, 2, 3, 52, 81, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {2, 3, 56, 82, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {3, 60, 83, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 32, 33, 68, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 36, 37, 64, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 40, 41, 65, 70, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 7, 44, 45, 66, 71, 80, -1, -1, -1, -1, -1, -1, -1}, {4, 5, 6, 7, 48, 49, 67, 81, 84, -1, -1, -1, -1, -1, -1, -1}, {5, 6, 7, 52, 53, 82, 85, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {6, 7, 56, 57, 83, 86, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {7, 60, 61, 87, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 32, 33, 34, 72, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 9, 36, 37, 38, 68, 73, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {8, 9, 10, 40, 41, 42, 64, 69, 74, 80, -1, -1, -1, -1, -1, -1}, {8, 9, 10, 11, 44, 45, 46, 65, 70, 75, 81, 84, -1, -1, -1, -1}, {8, 9, 10, 11, 48, 49, 50, 66, 71, 82, 85, 88, -1, -1, -1, -1}, {9, 10, 11, 52, 53, 54, 67, 83, 86, 89, -1, -1, -1, -1, -1, -1}, {10, 11, 56, 57, 58, 87, 90, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {11, 60, 61, 62, 91, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {12, 32, 33, 34, 35, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {12, 13, 36, 37, 38, 39, 72, 77, 80, -1, -1, -1, -1, -1, -1, -1}, {12, 13, 14, 40, 41, 42, 43, 68, 73, 78, 81, 84, -1, -1, -1, -1}, {12, 13, 14, 15, 44, 45, 46, 47, 64, 69, 74, 79, 82, 85, 88, -1}, {12, 13, 14, 15, 48, 49, 50, 51, 65, 70, 75, 83, 86, 89, 92, -1}, {13, 14, 15, 52, 53, 54, 55, 66, 71, 87, 90, 93, -1, -1, -1, -1}, {14, 15, 56, 57, 58, 59, 67, 91, 94, -1, -1, -1, -1, -1, -1, -1}, {15, 60, 61, 62, 63, 95, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {16, 32, 33, 34, 35, 80, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {16, 17, 36, 37, 38, 39, 76, 81, 84, -1, -1, -1, -1, -1, -1, -1}, {16, 17, 18, 40, 41, 42, 43, 72, 77, 82, 85, 88, -1, -1, -1, -1}, {16, 17, 18, 19, 44, 45, 46, 47, 68, 73, 78, 83, 86, 89, 92, -1}, {16, 17, 18, 19, 48, 49, 50, 51, 64, 69, 74, 79, 87, 90, 93, -1}, {17, 18, 19, 52, 53, 54, 55, 65, 70, 75, 91, 94, -1, -1, -1, -1}, {18, 19, 56, 57, 58, 59, 66, 71, 95, -1, -1, -1, -1, -1, -1, -1}, {19, 60, 61, 62, 63, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 33, 34, 35, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 21, 37, 38, 39, 85, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {20, 21, 22, 41, 42, 43, 76, 86, 89, 92, -1, -1, -1, -1, -1, -1}, {20, 21, 22, 23, 45, 46, 47, 72, 77, 87, 90, 93, -1, -1, -1, -1}, {20, 21, 22, 23, 49, 50, 51, 68, 73, 78, 91, 94, -1, -1, -1, -1}, {21, 22, 23, 53, 54, 55, 69, 74, 79, 95, -1, -1, -1, -1, -1, -1}, {22, 23, 57, 58, 59, 70, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {23, 61, 62, 63, 71, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 34, 35, 88, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 38, 39, 89, 92, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 42, 43, 90, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 27, 46, 47, 76, 91, 94, -1, -1, -1, -1, -1, -1, -1}, {24, 25, 26, 27, 50, 51, 72, 77, 95, -1, -1, -1, -1, -1, -1, -1}, {25, 26, 27, 54, 55, 73, 78, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {26, 27, 58, 59, 74, 79, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {27, 62, 63, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 35, 92, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 39, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 43, 94, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 31, 47, 95, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {28, 29, 30, 31, 51, 76, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {29, 30, 31, 55, 77, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {30, 31, 59, 78, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, {31, 63, 79, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};
    int maxBranching = 64;
    int maxDepth = 3;
    Color me;
    Color notMe;


    class BoardAnalysis {
        List<Move> legalMoves;
        Color winner;
        int[] whiteRuns;
        int[] blackRuns;
        float valueToBlack;
        float valueToWhite;
        int longestWhiteRun;
        int longestBlackRun;

        BoardAnalysis (List<Move> legalMoves, Color winner, int[] whiteRuns, int[] blackRuns, int longestWhiteRun, int longestBlackRun){
            this.whiteRuns = whiteRuns;
            this.blackRuns = blackRuns;
            this.legalMoves = legalMoves;
            this.winner = winner;
            this.longestWhiteRun = longestWhiteRun;
            this.longestBlackRun = longestBlackRun;
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

            if ( bd.legalMoves.size() ==62){
                System.out.println("*** Second mvoe debugr: " + bd.legalMoves);
                float aVal = debugmoveHeuristic(board,new Move(2,0),this.me);
                float bVal = debugmoveHeuristic(board,new Move(3,0),this.me);
                float cVal = debugmoveHeuristic(board,new Move(0,1),this.me);
                System.out.println("Vals: " + aVal + ":" + bVal + ":" + cVal);
            }

            //while (System.currentTimeMillis() < startTime + 9800) { }
            bestMove = alphaBetaSearch(board,this.me);
            //bestMove = bd.legalMoves.get(0);
            Color[][] cloneBoard = deepCloneBoard(board);

            cloneBoard[bestMove.row][bestMove.col] = this.me;

            BoardAnalysis bd2= boardAnalyser(cloneBoard);
            System.out.println("Value of this state: " + stateHeuristic(cloneBoard, this.me, this.notMe));
            if (bd2.winner == this.me)
                System.out.println("My win predictor thinks this will win " + bd2.winner);
            return bestMove;

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            return bestMove; // Might get lucky and have a valid move to return
        }
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

    //private Move chooseMove(Color[][] board, Color me, List<Move>  legalMoves, long startTime){ }

    boolean doesMoveWin(Color[][] board, Move testMove, Color me){
        // Checks if given move will win on given board
        Color[][] cloneBoard = deepCloneBoard(board);
        cloneBoard[testMove.row][testMove.col] = me;
        BoardAnalysis bd= boardAnalyser(cloneBoard);
        if (bd.winner == me) {
            // If that move wins
            return true;
        }
        return false;
    }

    private BoardAnalysis boardAnalyser(Color[][] board){
        // Checks for end of game and returns legalmoves if not ended
        int[] whiteRuns = new int[96];
        int[] blackRuns = new int[96];
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        List<Move> legalMoves = new ArrayList<>();
        //System.out.println("*** DEBUG *** ");
        // Looping through each position is expensive so try to do only once
        for (int col = 0; col < 8; col++ ) {
            for (int row = 0; row < 8; row++) {
                // For each square
                int squareID = row * 8 + col;
                Color token = board[row][col];
                if (token == null) {
                    // For every empty square
                    legalMoves.add(new Move(row, col));
                    continue;
                }
                for(int runNo = 0; this.allRuns[squareID][runNo] != -1; ++runNo) {
                    // For each possible run
                    if (token == Color.white) {
                        // For every white square
                        //System.out.println("White incrementing with a run of: " + whiteRuns[this.allRuns[squareID][runNo]]);
                        longestWhiteRun = Math.max(++whiteRuns[this.allRuns[squareID][runNo]], longestWhiteRun);
                        if (longestWhiteRun >= 5) {
                            //System.out.println("DEBUG " + squareID + ":"+  row + ":" + col + ":" + runNo );
                            //System.out.println("White wins with a run of: " + whiteRuns[this.allRuns[squareID][runNo]]);
                            return new BoardAnalysis(legalMoves, Color.white, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun);
                        }
                    }
                    if (token == Color.black) {
                        // For every black square
                        longestBlackRun = Math.max(++blackRuns[this.allRuns[squareID][runNo]], longestBlackRun);
                        if (longestBlackRun >= 5) {
                            //System.out.println("Black wins with a run of: " + blackRuns[this.allRuns[squareID][runNo]]);
                            return new BoardAnalysis(legalMoves, Color.black, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun);
                        }
                    }

                }
            }
        }
        return new BoardAnalysis(legalMoves, null, whiteRuns, blackRuns, longestWhiteRun, longestBlackRun );
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
            return Math.round(moveHeuristic(this.board, a, this.me) - moveHeuristic(this.board, b, this.me));
        }
    }

    private int getMaxRun(Color[][] board, Color player, Move move){
        //check for vertical win
        //check for horizontal win
        // check for topleft to bottom right diagonal win
        // check for bottom left to top right diagonal win
        return 0;
    }

    private List<Move>  genHorizontalMoves(Move move){
        // Generate horizontal pos
        List<Move> horizontalMoves = new ArrayList<>();
        for (int col = 0; col < 8; col++ ){
            horizontalMoves.add( new Move(move.row,col));
        }
        return  horizontalMoves;
    }

    private List<Move>  genVerticalMoves(Move move){
        // Generate horizontal pos
        List<Move> verticalMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++ ){
            verticalMoves.add( new Move(row,move.col));
        }
        return  verticalMoves;
    }

    private List<Move>  genTLBRDiagMoves(Move move){
        // Generate horizontal pos
        List<Move> diagMoves = new ArrayList<>();
        int yintercept = move.col - move.row;
        int startingCol = Math.max(0,-yintercept);
        int row;

        for (int col = startingCol; col < 8 && col + yintercept < 8; col++ ){
            row = col + yintercept;
            diagMoves.add( new Move(row,col));
        }
        return  diagMoves;
    }

    private List<Move>  genBLTRDiagMoves(Move move){
        // Generate horizontal pos
        List<Move> diagMoves = new ArrayList<>();
        int yintercept = move.col + move.row;
        int startingCol = Math.max(0,-yintercept);
        int row;

        for (int col = startingCol; col < 8 && -col + yintercept < 8; col++ ){
            row = -col + yintercept;
            diagMoves.add( new Move(row,col));
        }
        return  diagMoves;
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

        if (player == Color.black){
            value = -value;
        }
        //System.out.println("Value: " + value + " whitescore: " + whiteScore + " blackscore: " + blackScore);
        //System.out.println("Value: " + value + " board: " + Arrays.toString(board) + me);
        return value;
    }

    private float debugstateHeuristic(Color[][] board, Color player, Color nextMove){
        BoardAnalysis bd = boardAnalyser(board);

        // Gives advantage to those that are playing next
        double initiative = (nextMove == Color.white) ? 0.5 : -0.5;

        // Want a run of 5 to be extemely valuable and a run of 4 to be greatly more valuable than a 3
        double whiteScore = 1/(6.01-(bd.longestWhiteRun + initiative));
        double blackScore = 1/(6.01-(bd.longestBlackRun - initiative));

        float value = (float) (whiteScore - blackScore) / (float) (whiteScore + blackScore);
        bd.valueToWhite = value;
        bd.valueToBlack = -value;

        if (player == Color.black){
            value = -value;
        }
        System.out.println("Value: " + value + " whitescore: " + whiteScore + " blackscore: " + blackScore);
        System.out.println("Whiterun: " + bd.longestWhiteRun);
        System.out.println("Blackrun: " + bd.longestBlackRun);

        return value;
    }

    private float debugmoveHeuristic(Color[][] board, Move move, Color player){
        Color[][] cloneBoard = deepCloneBoard(board);
        cloneBoard[move.row][move.col] = player;
        Color nextPLayer = (player == Color.white) ? Color.black : Color.white;

        // returns a state value between -1 and 1. -1 indicates i lose
        float value = debugstateHeuristic(cloneBoard, player, nextPLayer);
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

    private Move alphaBetaSearch(Color[][] board, Color me){
        BoardAnalysis bd = boardAnalyser(board);
        Move bestMove = bd.legalMoves.get(0);
        float bestVal = -2;
        Color minColor = (me == Color.white) ? Color.black : Color.white;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++){
            //System.out.println("***Trying different top level action: " + i);
            Move legalMove = bd.legalMoves.get(i);
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = me;
            float minVal = minABValue(board, me, minColor, -2, 2, this.maxDepth);
            if ( minVal > bestVal){
                bestVal = minVal;
                bestMove = legalMove;
            }
        }
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
            return stateHeuristic(board, minColor, minColor);
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

/*

    private int maxValue(Color[][] board, Color maxColor, Color minColor){

        BoardAnalysis bd = boardAnalyser(board);
        if (bd.winner != null) {
            return -1; // stand in for infinity
        }
        List<Integer> values = new ArrayList<Integer>();;
        for (Move legalMove : bd.legalMoves){
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = maxColor;
            values.add(minValue(cloneBoard, maxColor, minColor));
        }
        return Collections.max(values);
    }

    private int minValue(Color[][] board, Color maxColor, Color minColor){

        BoardAnalysis bd = boardAnalyser(board);
        if (bd.winner != null) {
            return 1; // stand in for infinity
        }
        List<Integer> values = new ArrayList<Integer>();;
        for (Move legalMove : bd.legalMoves){
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = minColor;
            values.add(maxValue(cloneBoard, maxColor, minColor));
        }
        return Collections.min(values);
    }

    private Move minimaxDecision(Color[][] board, Color me){
        BoardAnalysis bd = boardAnalyser(board);
        Move bestMove = bd.legalMoves.get(0);
        int bestVal = -2;
        Color minColor = (me == Color.white) ? Color.black : Color.white;

        for (Move legalMove : bd.legalMoves){
            Color[][] cloneBoard = deepCloneBoard(board);
            cloneBoard[legalMove.row][legalMove.col] = me;
            int minVal = minValue(board, me, minColor);
            if ( minVal > bestVal){
                bestVal = minVal;
                bestMove = legalMove;
            }
        }
        return  bestMove;
    }

    /*
    private int[] minimax(int player, List<Move>  legalMoves, Color[][] board, Color me){
        int winner;
        int row = col = -1;


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
