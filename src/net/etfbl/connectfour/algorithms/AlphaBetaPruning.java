package net.etfbl.connectfour.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Game.Player;

public class AlphaBetaPruning extends Algorithm{
	private static final Double alphaNuclearOption = Double.NEGATIVE_INFINITY;
	private static final Double betaNuclearOption = Double.POSITIVE_INFINITY;
    
	Player Max;
	Player Min;
	Move bestMove;
	
	int evalType;
	
	List<Integer> probabilityDistribution = Arrays.asList(1, 2, 3, 4, 3, 2, 1);
	
	public AlphaBetaPruning(int type) {
		evalType = type;
	}
	
	@Override
	public Move getIdealMove(GameBoard board, Player player, int depth) {
		System.out.println("START AlphaBeta for " + player + "; algo type: [" + this.evalType + "]");
		return alphaBetaDecision(board, player, depth);
	}
	
	public Move alphaBetaDecision(GameBoard board, Player player, int depth) {
		this.Max = player;
		this.Min = getReversePlayer(player);
		
		Move initalMove = new Move(-1, -1);
		
		System.out.println("Starting AlphaBeta - Max: " + Max + "; Min: " + Min);
		
		int value = maxAlphaBetaValue(board, initalMove, player, depth, alphaNuclearOption, betaNuclearOption, true);
		
		System.out.println("Ending AlphaBeta value: " + value);
		
		return this.bestMove;
	}
	
	private int maxAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta, boolean isRootCall) {
        Integer terminalState = !isRootCall ? board.checkTerminalState(previousMove, previousPlayer, this.Max) : null;
        int lowerDepth = depth - 1;
        
        if(terminalState != null) {
        	return terminalState * depth;
        } else if(lowerDepth <= 0) {
            int eval;
            if(evalType == 1) {
//        		System.out.println("MAX Using improved for player : " + Max);
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer);
//        		eval = (int) Math.round(Heuristics.stateEvaluationConnectFourGaussian(new GameBoard(board.getBoard()), previousPlayer) / 10);
        	} else {
//        		System.out.println("MAX Using BAAADD for player : " + Max);
        		eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
        	}
            
            return eval;
        }

        double score = Double.NEGATIVE_INFINITY;
        List<Move> possibleStateActions = board.stateActions();
        GameBoard tempActionState;
        Move currentState;
        
        for(int i = 0; i < possibleStateActions.size(); i++) {
        	currentState = possibleStateActions.get(i);
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, this.Max);
            score = Math.max(score, minAlphaBetaValue(tempActionState, currentState, this.Max, lowerDepth, alpha, beta));


            // Ako je ovo zadovoljeno onda to ne odgovara MINu i dalje vrijednosti score-a nece ni gledati
            // Posto je score uvijek Math.max, moze samo gore biti po beti
            if(score >= beta)
                return (int) score;

            //Prune branch if true
            //if(alpha >= beta)
            //    return alpha;

            if(score > alpha) {
                if(isRootCall) {
                    this.bestMove = new Move(currentState.getRow(), currentState.getColumn());
                }

                alpha = score;
            }
        }

        return (int) score;
        //return alpha;
    };
    
    private int minAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta) {
        Integer terminalState = board.checkTerminalState(previousMove, previousPlayer, this.Max);
        int lowerDepth = depth - 1;

        if(terminalState != null) {
        	return terminalState * depth;
        } else if(lowerDepth <= 0) {
            int eval;
            if(evalType == 1) {
//        		System.out.println("MAX Using improved for player : " + Max);
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer);
//        		eval = (int) Math.round(Heuristics.stateEvaluationConnectFourGaussian(new GameBoard(board.getBoard()), previousPlayer) / 10);
        	} else {
//        		System.out.println("MAX Using BAAADD for player : " + Max);
        		eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
        	}
            
            return eval;
        }

        double score = Double.POSITIVE_INFINITY;
        List<Move> possibleStateActions = board.stateActions();
        GameBoard tempActionState;
        Move currentState;
        
        for(int i = 0; i < possibleStateActions.size(); i++) {
        	currentState = possibleStateActions.get(i);
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, this.Min);
            score = Math.min(score, maxAlphaBetaValue(tempActionState, currentState, this.Min, lowerDepth, alpha, beta, false));

            // Ako je ovo zadovoljeno onda to ne odgovara MAXu i dalje vrijednosti score-a nece ni gledati
            // Posto je score minimum, moze samo gore biti po alfi
            if(score <= alpha)
                return (int) score;

            beta = Math.min(beta, score);
        }

        return (int) score;
    };
	
	public Player getReversePlayer(Player player) {
        if(player.equals(Player.RED)) {
            return Player.YELLOW;
        } else {
            return Player.RED;
        }
    }
}
