import java.awt.Color;
import java.util.*;

class MiniMaxPlayer180789269 extends GomokuPlayer {
    // An Alpha-Beta Pruning minimax agent to play the game of gomoku.
    // Relies on storing information about visited states to allow for deeper search
    int maxBranching = 64;
    int maxDepth = 5;
    int moveCounter = -1;
    Color me;
    Color notMe;
    HashMap<String, BoardAnalysis180789269> analysedBoards = new HashMap<String, BoardAnalysis180789269>();
    List<Move> consideredMoves;
    long startTime;

    class BoardAnalysis180789269 {
        // Board analysis class provides key information on a given board
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

        BoardAnalysis180789269(List<Move> legalMoves, Color winner, int longestWhiteRun, int longestBlackRun, String boardID, Color[][] board) {
            // Constructor
            this.legalMoves = legalMoves;
            this.winner = winner;
            this.longestWhiteRun = longestWhiteRun;
            this.longestBlackRun = longestBlackRun;
            this.boardID = boardID;
            this.board = board;
        }
    }

    public Move chooseMove(Color[][] board, Color me) {
        // Chooses move based on result of Alpha-Beta pruned minimax search
        Move bestMove = new Move(1, 1);
        try {
            this.startTime = System.currentTimeMillis();
            this.me = me;
            this.notMe = (me == Color.white) ? Color.black : Color.white;
            ++this.moveCounter;

            BoardAnalysis180789269 bd = boardAnalyser(board);
            bd.legalMoves = reorderMovesByHeuristic(board, this.me, bd.legalMoves);

            //hardcoded first moves, found to be best in experimentation
            if (board[4][4] == null) {
                return new Move(4, 4);
            } else if (board[3][4] == null) {
                return new Move(3, 4);
            }

            // Perform alpha-beta pruned minimax search
            bestMove = alphaBetaSearch(board, this.me, bd);
            System.out.println("Time taken by MiniMaxPlayer180789269 in millis: " + (System.currentTimeMillis() - this.startTime));
            return bestMove;

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            return bestMove; // Might get lucky and have a valid move to return
        }
    }

    private BoardAnalysis180789269 boardAnalyser(Color[][] board) {
        // Checks for end of game, finds legal moves, finds the number of runs for each player
        // Also saves the analysis to the analysedBoard hashmap so this process can be skipped in future
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        String boardID = "";
        List<Move> legalMoves = new ArrayList<>();
        BoardAnalysis180789269 bd;

        // Looping through each position is expensive so try to do only once
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color token = board[row][col];
                // For each square
                int squareID = row * 8 + col;
                if (token == null) {
                    // For every empty square
                    // We find both the legalmoves and create the boardID to avoid looping more thna once
                    legalMoves.add(new Move(row, col));
                    boardID = boardID + "0";
                    continue;
                } else if (token == Color.white) {
                    boardID = boardID + "w";
                } else {
                    boardID = boardID + "b";
                }
            }
        }

        // If this board has already been analysed then load the analysis object
        if (this.analysedBoards.containsKey(boardID)) {
            return this.analysedBoards.get(boardID);
        }

        // Get the longest runs for each colour anc check for a winner
        longestWhiteRun = getMaxRunForBoard(board, Color.white);
        if (longestWhiteRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.white, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID, bd);
            return bd;
        }
        longestBlackRun = getMaxRunForBoard(board, Color.black);
        if (longestBlackRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.black, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID, bd);
            return bd;
        }

        // If no winner, return null for winner with all other analysis information
        bd = new BoardAnalysis180789269(legalMoves, null, longestWhiteRun, longestBlackRun, boardID, board);
        this.analysedBoards.put(boardID, bd);
        return bd;
    }

    private BoardAnalysis180789269 fasterBoardAnalyser(String prevBoardID, Move move, Color moveColor) {
        // Performs same as boardanalyser but uses information from previous board state to make it faster
        int longestWhiteRun = 0;
        int longestBlackRun = 0;
        String boardID = "";
        List<Move> legalMoves;
        BoardAnalysis180789269 bd;

        // Load previous boards analysis
        BoardAnalysis180789269 prevbd = this.analysedBoards.get(prevBoardID);
        Color[][] board;

        // Find the board id for this board by applying the change to the id that the move will make
        int moveID = move.row * 8 + move.col;
        char changeID = (moveColor == Color.white) ? 'w' : 'b';
        boardID = prevBoardID.substring(0, moveID) + changeID + prevBoardID.substring(moveID + 1);

        // If we have already analysed this state then simply return that analysis
        if (this.analysedBoards.containsKey(boardID)) {
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

        // Check if white has won
        if (longestWhiteRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.white, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID, bd);
            return bd;
        }
        // Check if black has won
        if (longestBlackRun >= 5) {
            bd = new BoardAnalysis180789269(legalMoves, Color.black, longestWhiteRun, longestBlackRun, boardID, board);
            this.analysedBoards.put(boardID, bd);
            return bd;
        }

        // If no one has won, return analysis with winner as null
        bd = new BoardAnalysis180789269(legalMoves, null, longestWhiteRun, longestBlackRun, boardID, board);

        // Save this analysis to the analysedBoards hashmap for later use
        this.analysedBoards.put(boardID, bd);
        return bd;
    }

    private int getMaxRunForBoard(Color[][] board, Color player) {
        // This finds the maximum run for a player on a board
        int maxRun = 0;
        // By searching the below combination of positions for their max runs, we search the whole board without redundency

        // Gets all runs for top left corner, which includes top row, first column and central diagonal
        maxRun = Math.max(maxRun, getMaxRunForPosition(board, player, new Move(0, 0)));

        // All horizontals and diagonals runs for moves in second column
        for (int col = 1; col < 8; col++) {
            maxRun = Math.max(maxRun, genVerticalMaxRun(board, player, new Move(0, col)));
            maxRun = Math.max(maxRun, genTLBRDiagMaxRun(board, player, new Move(0, col)));
            maxRun = Math.max(maxRun, genBLTRDiagMaxRun(board, player, new Move(0, col)));
        }

        // All verticals and diagonal runs for positions in second row
        for (int row = 1; row < 8; row++) {
            maxRun = Math.max(maxRun, genHorizontalMaxRun(board, player, new Move(row, 0)));
            maxRun = Math.max(maxRun, genTLBRDiagMaxRun(board, player, new Move(row, 0)));
            maxRun = Math.max(maxRun, genBLTRDiagMaxRun(board, player, new Move(row, 0)));
        }
        return maxRun;
    }

    private int getMaxRunForPosition(Color[][] board, Color player, Move move) {
        // Gives the maximum run for given player, searching only positions connected to given move
        int max1 = Math.max(genHorizontalMaxRun(board, player, move), genVerticalMaxRun(board, player, move));
        int max2 = Math.max(genTLBRDiagMaxRun(board, player, move), genBLTRDiagMaxRun(board, player, move));

        return Math.max(max1, max2);
    }

    private int maxRunFromSeq(Color[][] board, Color player, List<Move> movSeq) {
        // From a list of moves, find max continous run
        int maxRun = 0;
        int currentRun = 0;
        for (Move move : movSeq) {
            // If the move is not the player, reset the counter
            if (board[move.row][move.col] != player) {
                currentRun = 0;
                continue;
            }
            //Increment counter and maintain max run
            currentRun++;
            maxRun = Math.max(maxRun, currentRun);
        }
        return maxRun;
    }

    private int genHorizontalMaxRun(Color[][] board, Color player, Move move) {
        // Generate list of moves on same horizontal as this move
        List<Move> movSeq = new ArrayList<>();
        for (int col = 0; col < 8; col++) {
            movSeq.add(new Move(move.row, col));
        }
        return maxRunFromSeq(board, player, movSeq);
    }

    private int genVerticalMaxRun(Color[][] board, Color player, Move move) {
        // Generate list of moves on same vertical as this move
        List<Move> movSeq = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            movSeq.add(new Move(row, move.col));
        }
        return maxRunFromSeq(board, player, movSeq);
    }

    private int genTLBRDiagMaxRun(Color[][] board, Color player, Move move) {
        // Generate a list of positions on Top Left to Bottom Right (TLBR) diagonal of this move
        List<Move> movSeq = new ArrayList<>();
        int yintercept = move.col - move.row;
        int startingCol = Math.max(0, -yintercept);
        int row;

        for (int col = startingCol; col < 8 && (col + yintercept) < 8; col++) {
            row = col + yintercept;
            movSeq.add(new Move(row, col));
        }
        return maxRunFromSeq(board, player, movSeq);
    }

    private int genBLTRDiagMaxRun(Color[][] board, Color player, Move move) {
        // Generate a list of positions on Bottom Left to Top Right (BLTR) diagonal of this move
        List<Move> movSeq = new ArrayList<>();
        int yintercept = move.col + move.row;
        int startingCol;
        int row;

        if (yintercept >= 7) {
            startingCol = yintercept - 7;
        } else {
            startingCol = 0;
        }

        for (int col = startingCol; col < 8 && (-col + yintercept) >= 0; col++) {
            row = -col + yintercept;
            movSeq.add(new Move(row, col));
        }
        return maxRunFromSeq(board, player, movSeq);
    }

    private String getBoardID(Color[][] board) {
        // Creates an ID which represents the board for retrieval from analysedBoards hashmap
        String boardID = "";

        // For each square
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color token = board[row][col];
                int squareID = row * 8 + col;
                if (token == null) {
                    // For every empty square
                    boardID = boardID + "0";
                    continue;
                } else if (token == Color.white) {
                    boardID = boardID + "w";
                } else {
                    boardID = boardID + "b";
                }
            }
        }
        return boardID;
    }

    private float moveHeuristic(Color[][] board, Move move, Color player) {
        // returns a state value between -1 and 1. -1
        Color nextPLayer = (player == Color.white) ? Color.black : Color.white;

        //float value = fastStateHeuristic(board, player, nextPLayer, move);
        float value = getMaxRunForPosition(board, player, move) / 10;
        return value;
    }

    private List<Move> reorderMovesByHeuristic(Color[][] board, Color me, List<Move> legalMoves) {
        CompareMoves180789269 comparator = new CompareMoves180789269(board, me);
        Collections.sort(legalMoves, comparator);
        return legalMoves;
    }

    class CompareMoves180789269 implements Comparator<Move> {
        // Class to allow sorting of moves
        Color[][] board;
        Color me;

        CompareMoves180789269(Color[][] board, Color me) {
            // Constructor
            this.board = board;
            this.me = me;
        }

        public int compare(Move a, Move b) {
            // Sorts in descending order, i.e highest val will be first now
            float comp = moveHeuristic(this.board, b, this.me) - moveHeuristic(this.board, a, this.me);
            if (comp > 0) {
                return 1;
            } else if (comp < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private Move alphaBetaSearch(Color[][] board, Color me, BoardAnalysis180789269 bd) {
        // Top level Alpha-beta search function
        Move bestMove = bd.legalMoves.get(0);
        float bestVal = -2;
        float beta = 2;
        float alpha = -2;
        Color minColor = (me == Color.white) ? Color.black : Color.white;

        // For each legal move
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++) {
            Move legalMove = bd.legalMoves.get(i);
            float minVal = minABValue(bd.boardID, legalMove, me, minColor, alpha, beta, this.maxDepth);

            // Maintain max score and best move
            if (minVal > bestVal) {
                bestVal = minVal;
                bestMove = legalMove;
            }

            // Perform pruning similar to maxABValue function
            if (minVal >= beta) {
                return bestMove;
            }
            alpha = Math.max(alpha, minVal);
        }
        return bestMove;
    }

    private float maxABValue(String boardID, Move lastMove, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining) {
        // Gets best case score for the maxplayer with alpha beta pruning
        float value = -2;
        BoardAnalysis180789269 bd = fasterBoardAnalyser(boardID, lastMove, minColor);
        if (bd.winner != null) {
            return value;
        }
        --depthRemaining;
        if (depthRemaining < 1) {
            return 0; // stateHeuristic(bd.board, maxColor, maxColor);
        }
        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++) {
            Move legalMove = bd.legalMoves.get(i);
            value = Math.max(value, minABValue(bd.boardID, legalMove, maxColor, minColor, alpha, beta, depthRemaining));
            if (value >= beta) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private float minABValue(String boardID, Move lastMove, Color maxColor, Color minColor, float alpha, float beta, int depthRemaining) {
        // Gets best case score for the minplayer with alpha beta pruning
        float value = 2;
        BoardAnalysis180789269 bd = fasterBoardAnalyser(boardID, lastMove, maxColor);
        if (bd.winner != null) {
            return value; // stand in for infinity
        }
        --depthRemaining;
        if (depthRemaining < 1) {
            return 0; //stateHeuristic(bd.board, maxColor, minColor);
        }
        List<Integer> values = new ArrayList<Integer>();
        ;
        for (int i = 0; i < bd.legalMoves.size() && i < this.maxBranching; i++) {
            Move legalMove = bd.legalMoves.get(i);
            value = Math.min(beta, maxABValue(bd.boardID, legalMove, maxColor, minColor, alpha, beta, depthRemaining));
            if (value <= alpha) {
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    private Color[][] deepCloneBoard(Color[][] board) {
        // Performs a deep clone on the board object
        Color[][] cloneBoard = new Color[8][8];
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                cloneBoard[row][col] = board[row][col];
            }
        }
        return cloneBoard;
    }


    private float fastStateHeuristic(Color[][] board, Color player, Color nextMove, Move move) {
        // Returns a measure of the value of a board state to the player, uses fastboardAnalyser
        // DEPRECATED as too slow

        BoardAnalysis180789269 bd = fasterBoardAnalyser(getBoardID(board), move, player);
        // If the player has won in this scenario then we want it to have the largest value
        if (bd.winner == player) {
            return 2;
        }
        if (bd.winner != null) {
            return -2;
        }
        // Gives advantage to those that are playing next
        double initiative = (nextMove == Color.white) ? 0.5 : -0.5;
        // Want a run of 5 to be extemely valuable and a run of 4 to be greatly more valuable than a 3
        double whiteScore = 1 / (6.01 - (bd.longestWhiteRun + initiative));
        double blackScore = 1 / (6.01 - (bd.longestBlackRun - initiative));

        float value = (float) (whiteScore - blackScore) / (float) (whiteScore + blackScore);
        bd.valueToWhite = value;
        bd.valueToBlack = -value;
        this.analysedBoards.put(bd.boardID, bd);

        if (player == Color.black) {
            value = -value;
        }
        return value;
    }

    private float stateHeuristic(Color[][] board, Color player, Color nextMove) {
        // Returns a measure of the value of a board state to the player
        // DEPRECATED as too slow
        BoardAnalysis180789269 bd = boardAnalyser(board);
        // If the player has won in this scenario then we want it to have the largest value
        if (bd.winner == player) {
            return 2;
        }
        if (bd.winner != null) {
            return -2;
        }
        // Gives advantage to those that are playing next
        double initiative = (nextMove == Color.white) ? 0.5 : -0.5;
        // Want a run of 5 to be extemely valuable and a run of 4 to be greatly more valuable than a 3
        double whiteScore = 1 / (6.01 - (bd.longestWhiteRun + initiative));
        double blackScore = 1 / (6.01 - (bd.longestBlackRun - initiative));

        float value = (float) (whiteScore - blackScore) / (float) (whiteScore + blackScore);
        bd.valueToWhite = value;
        bd.valueToBlack = -value;
        this.analysedBoards.put(bd.boardID, bd);

        if (player == Color.black) {
            value = -value;
        }
        //System.out.println("Value: " + value + " whitescore: " + whiteScore + " blackscore: " + blackScore);
        //System.out.println("Value: " + value + " board: " + Arrays.toString(board) + me);
        // TODO this is essentially getting us to ignore the value of the state
        return value;
    }

    private List<Move> trimLegalMoves(List<Move> legalMoves) {
        // Removes moves that are not relevant, asummes they've already been sorted
        // DEPRECATED as ignoring potentially strong moves was not worth the performance increase
        List<Move> trimmedMoves;
        // remove moves that irrelevent in the early game
        if (legalMoves.size() >= 61) {
            trimmedMoves = legalMoves.subList(0, 45);
        } else if (legalMoves.size() >= 40) {
            trimmedMoves = legalMoves.subList(0, 40);
        } else {
            trimmedMoves = legalMoves;
        }
        return trimmedMoves;
    }
}

