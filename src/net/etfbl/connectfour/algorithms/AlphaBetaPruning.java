package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Utility;
import net.etfbl.connectfour.Game.Player;

public class AlphaBetaPruning extends Algorithm{
	private static final Double alphaNuclearOption = Double.NEGATIVE_INFINITY;
	private static final Double betaNuclearOption = Double.POSITIVE_INFINITY;
	private static final List<Integer> probabilityDistribution = Arrays.asList(1, 2, 3, 4, 3, 2, 1);
    
	private Player Max;
	private Player Min;
	private Move bestMove;
	private List<Move> maxMoves = new ArrayList<Move>();
	private int currentPly;
	private boolean isUnevaluatedMove;
	
	private int evalType;
	
	public AlphaBetaPruning(Player maxPlayer, int type) {
		this.Max = maxPlayer;
		this.Min = Utility.oppositePlayer(maxPlayer);
		this.evalType = type;
	}
	
	@Override
	public Move getIdealMove(GameBoard board, int depth, int ply) {
//		System.out.println("START AlphaBeta for " + player + "; algo type: [" + this.evalType + "]");
//		this.Max = player;
//		this.Min = getReversePlayer(player);
		this.bestMove = null;
		this.maxMoves.clear();
		this.currentPly = ply;
		this.isUnevaluatedMove = false;
		
		return alphaBetaDecision(board, depth);
	}
	
	public Move alphaBetaDecision(GameBoard board, int depth) {
		Move initalMove = new Move(-1, -1);
		Integer value = null;
		
		System.out.println("Starting *AlphaBeta* decision - Max: " + Max + "; Min: " + Min + "; Depth: " + depth + "; Ply: " + currentPly);
		
		/**
		 * Prva dva poteza je tesko evaluirati algoritmom zato se na osnovu prethdnog iskustva uzimaju predoredjeni potezi.
		 * 
		 */
		if(this.currentPly < 3) {
			return Heuristics.evaluatedStartingMoveConnectFour(board, Max, currentPly);
		} else {
			value = maxAlphaBetaValue(board, initalMove, this.Max, depth, alphaNuclearOption, betaNuclearOption, true);
		}
		
		if(this.currentPly < 7 && this.isUnevaluatedMove) {
			System.out.println("ALPHA BETA - mid-game zero score getting random move based on distribution...");
			this.isUnevaluatedMove = false;
			this.bestMove = Heuristics.evaluatedMidStartMoveConnectFour(board, Max, currentPly);
			System.out.println("Best move [" + this.bestMove.getRow() + "][" + this.bestMove.getColumn() +"]");
			
			return this.bestMove;
		}
		
		/*System.out.println("Ending AlphaBeta value: " + value + " Max moves size: " + this.maxMoves.size());
		        
        Move bestMoveFromMax = Utility.randomlyDistributedMove(maxMoves, probabilityDistribution);
        
        System.out.println("ALPHA BETA - Best move [" + bestMoveFromMax.getRow() + "][" + bestMoveFromMax.getColumn() +"]");
        
        return bestMoveFromMax;*/
		
		System.out.println("ALPHA BETA - Best move [" + this.bestMove.getRow() + "][" + this.bestMove.getColumn() +"]");
		
		return this.bestMove;
	}
	
	private int maxAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta, boolean isRootCall) {
        Integer terminalState = !isRootCall ? board.checkTerminalState(previousMove, previousPlayer, this.Max) : null;
        boolean check4Terminal = board.checkGameEnd(previousMove, previousPlayer);
        System.out.println("MAX terminal state : " + terminalState + "| check4Terminal : " + check4Terminal);
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
                return Utility.roundDouble(score);

            //Prune branch if true
            //if(alpha >= beta)
            //    return alpha;

//            if(isRootCall) {
//            	System.out.println("- ROOT (" + i + ") Score: " + score + " - Alpha: " + alpha + "; [" + currentState.getRow() + "][" + currentState.getColumn() +"]");
//            }
            
//            if(score >= alpha) {
        	if(score > alpha) {
                if(isRootCall) {
                	if((int) score == 0 && alpha == Double.NEGATIVE_INFINITY) {
                		this.isUnevaluatedMove = true;
                	} else if(this.isUnevaluatedMove) {
                		this.isUnevaluatedMove = false;
                	}
                	
                	System.out.println("+ ROOT (" + i + ") Score: " + score + " - Alpha: " + alpha + "; [" + currentState.getRow() + "][" + currentState.getColumn() +"]" + " - isUnevaluatedMove: " + this.isUnevaluatedMove);
                	/*if(score == alpha && this.currentPly < 4) {
                		System.out.println("0 ROOT (" + i + ") Score: " + score + " - Alpha: " + alpha + "; [" + currentState.getRow() + "][" + currentState.getColumn() +"]");
                		this.maxMoves.add(new Move(currentState.getRow(), currentState.getColumn()));
                	} else if(score > alpha){
                		this.maxMoves.clear();
                		this.maxMoves.add(new Move(currentState.getRow(), currentState.getColumn()));
                	}*/

                	 // uzima prvi najbolji potez
                    this.bestMove = new Move(currentState.getRow(), currentState.getColumn());
                }
                
                alpha = score;
            }
        }

        return Utility.roundDouble(score);
        //return alpha;
    };
    
    private int minAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta) {
        Integer terminalState = board.checkTerminalState(previousMove, previousPlayer, this.Max);
        boolean check4Terminal = board.checkGameEnd(previousMove, previousPlayer);
        System.out.println("MIN terminal state : " + terminalState + "| check4Terminal : " + check4Terminal);
        
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
                return Utility.roundDouble(score);

            beta = Math.min(beta, score);
        }

        return Utility.roundDouble(score);
    };
}
