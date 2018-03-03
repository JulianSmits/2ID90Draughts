package nl.tue.s2id90.group44;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 *
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyDraughtsPlayer_1 extends DraughtsPlayer {

    private int bestValue = 0;
    int maxSearchDepth;
    int currentSearchDepth = 1;
    int[] evalArray = new int[]{9, 10, 10, 10, 9, 6, 7, 8, 7, 6, 4, 5, 7, 5, 4,
        3, 4, 6, 6, 4, 3, 4, 5, 4, 3, 1, 3, 4, 3, 1, 1, 3, 3, 1, 0, 0, 1, 2, 1,
        0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0};

    /**
     * boolean that indicates that the GUI asked the player to stop thinking.
     */
    private boolean stopped;

    public MyDraughtsPlayer_1(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
    }

    @Override
    public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s.clone());    // the root of the search tree  
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);

            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()

        } catch (AIStoppedException ex) {
            /* nothing to do */        }

        bestMove = node.getBestMove();
        
        // print the results for debugging reasons
        System.err.format(
                "%s: depth= %2d, best move = %5s, value=%d\n",
                this.getClass().getSimpleName(), maxSearchDepth, bestMove, bestValue
        );

        if (bestMove == null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    }

    /**
     * This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to
     * getMove(s).
     */
    @Override
    public Integer getValue() {
        return bestValue;
    }

    /**
     * Tries to make alphabeta search stop. Search should be implemented such
     * that it throws an AIStoppedException when boolean stopped is set to true;
     *
     */
    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * returns random valid move in state s, or null if no moves exist.
     */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Implementation of alphabeta that automatically chooses the white player
     * as maximizing player and the black player as minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     *
     */
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (node.getState().isWhiteToMove()) {
            int returnValue = alphaBetaMax(node, alpha, beta, 1);
            for (int i = 2; i < depth; i++) {
                currentSearchDepth = i;
                returnValue = alphaBetaMax(node, alpha, beta, i);
                bestValue = returnValue;
            }
            return returnValue;
        } else {
            int returnValue = alphaBetaMin(node, alpha, beta, 1);
            for (int i = 2; i < depth; i++) {
                currentSearchDepth = i;
                returnValue = alphaBetaMin(node, alpha, beta, i);
                bestValue = returnValue;
            }
            return returnValue;
        }
    }

    /**
     * Does an alphabeta computation with the given alpha and beta where the
     * player that is to move in node is the minimizing player.
     *
     * <p>
     * Typical pieces of code used in this method are:
     * <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     * <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     * <li><code>node.setBestMove(bestMove);</code></li>
     * <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     * </ul>
     * </p>
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been
     * set to true.
     */
    int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        List<Move> moves = state.getMoves(); // get all the moves
        int lastValue = beta; // initialize starting values
        int value = beta;
        Move bestMove = moves.get(0);

        for (Move move : moves) { //loop over all the moves
            state.doMove(move);

            if (depth > 0 && !state.isEndState()) {
                value = Math.min(value, alphaBetaMax(node, alpha, value, depth - 1));
            } else {
                value = Math.min(value, evaluate(state));
            }
            if (value < lastValue) { // set better move as best move if possible
                bestMove = move;
                lastValue = value;
            }

            state.undoMove(move);
        }

        if (currentSearchDepth == depth) {
            node.setBestMove(bestMove);
            System.err.println("depth= " + depth);
        }
        return lastValue;
    }

    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        DraughtsState state = node.getState();

        List<Move> moves = state.getMoves(); // get all the moves
        int lastValue = alpha; // initialize starting values
        int value = alpha;
        Move bestMove = moves.get(0);

        for (Move move : moves) { //loop over all the moves
            state.doMove(move);
            if (depth > 0 && !state.isEndState()) {
                value = Math.max(value, alphaBetaMin(node, value, beta, depth - 1));
            } else {
                value = Math.max(value, evaluate(state));
            }
            if (value > lastValue) { // set better move as best move if possible
                bestMove = move;
                lastValue = value;
            }

            state.undoMove(move);
        }

        if (currentSearchDepth == depth) {
            node.setBestMove(bestMove);
            System.err.println("depth= " + depth);
        }
        return lastValue;
    }

    /**
     * A method that evaluates the given state.
     */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        int[] pieces = state.getPieces();
        int whitePieces = 0;
        int blackPieces = 0;
        for (int i = 0; i < pieces.length; i++) {
            switch (pieces[i]) {
                case 1:
                    whitePieces++;
                    break;
                case 2:
                    blackPieces++;
                    break;
                case 3:
                    whitePieces += 3;
                    break;
                case 4:
                    blackPieces += 3;
                    break;
            }
        }
        return whitePieces - blackPieces;
    }
}