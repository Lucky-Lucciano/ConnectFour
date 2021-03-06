package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Utility;
import net.etfbl.connectfour.Game.Player;

public class AlphaBetaPruning extends Algorithm{
	private static final Double alphaNuclearOption = Double.NEGATIVE_INFINITY;
	private static final Double betaNuclearOption = Double.POSITIVE_INFINITY;
    
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
		 * Prva dva poteza je tesko evaluirati(ocjeniti) algoritmom zato se na osnovu prethdnog iskustva uzimaju predodredjeni potezi.
		 * 
		 */
		if(this.currentPly < 3) {
			return Heuristics.evaluatedStartingMoveConnectFour(board, Max, currentPly);
		} else {
			value = maxAlphaBetaValue(board, initalMove, this.Max, depth, alphaNuclearOption, betaNuclearOption, true);
		}
		
		/**
		 * Ukoliko u prvih 6 poteza (12 ply) algoritam ne donese nikakvu odluku o svom narednom potezu,
		 * uzeti nasumičan potez na osnovu tabele evaluacije.
		 * 
		 */
		if(this.currentPly < 12 && this.isUnevaluatedMove) {
			System.out.println("ALPHA BETA - mid-game zero score getting random distribution based move...");
			this.isUnevaluatedMove = false;
			this.bestMove = Heuristics.evaluatedMidStartMoveConnectFour(board, Max, currentPly);
			System.out.println("Best move [" + this.bestMove.getRow() + "][" + this.bestMove.getColumn() +"]");
			
			return this.bestMove;
		}
		
		System.out.println("ALPHA BETA - Best move [" + this.bestMove.getRow() + "][" + this.bestMove.getColumn() +"]");
		
		return this.bestMove;
	}
	
	private int maxAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta, boolean isRootCall) {
        Integer terminalState = !isRootCall ? board.checkTerminalState(previousMove, previousPlayer, this.Max) : null;

        int lowerDepth = depth - 1;
        
        if(terminalState != null) {
        	/**
        	 * Vraca ocjenu terminalnog stanja pomnozenu sa dubinom, tako da "plici" potezi imaju bolju ocjenu
        	 */
        	return terminalState * depth;
        } else if(lowerDepth <= 0) {
            int eval = 0;
            if(evalType == 1) {
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer, true);
        	} else if(evalType == 2)  {
        		eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
        	} else if(evalType == 3) {
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer, false);
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

            // Ukoliko je uslov zadovoljen, Min-u to ne odgovara i dalje vrijednosti score-a nece ni gledati
            // Posto je score uvijek Math.max, moze samo gore biti po betu
            if(score >= beta) {
                return Utility.roundDouble(score);
            }
            
        	if(score > alpha) {
                if(isRootCall) {
                	if((int) score == 0 && alpha == Double.NEGATIVE_INFINITY) {
                		this.isUnevaluatedMove = true;
                	} else if(this.isUnevaluatedMove) {
                		this.isUnevaluatedMove = false;
                	}
                	
                	System.out.println("+ ROOT (" + i + ") Alpha: " + alpha + " - Score: " + score + "; [" + currentState.getRow() + "][" + currentState.getColumn() +"]" + " - unevaluated: " + this.isUnevaluatedMove);
                	// uzima prvi najbolji potez
                    this.bestMove = new Move(currentState.getRow(), currentState.getColumn());
                }
                
                alpha = score;
            }
        }

        return Utility.roundDouble(score);
    };
    
    private int minAlphaBetaValue(GameBoard board, Move previousMove, Player previousPlayer, int depth, double alpha, double beta) {
        Integer terminalState = board.checkTerminalState(previousMove, previousPlayer, this.Max);
        
        int lowerDepth = depth - 1;

        if(terminalState != null) {
        	return terminalState * depth;
        } else if(lowerDepth <= 0) {
            int eval = 0;
            if(evalType == 1) {
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer, true);
        	} else if(evalType == 2){
        		eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
        	} else if(evalType == 3){
        		eval = Heuristics.stateEvaluationConnectFourImproved(new GameBoard(board.getBoard()), previousPlayer, false);
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

            // Ukoliko je uslov zadovoljen, Max-u to ne odgovara i dalje vrijednosti score-a nece ni gledati
            // Posto je score minimum, moze samo gore biti po alfi
            if(score <= alpha) {
                return Utility.roundDouble(score);
            }

            beta = Math.min(beta, score);
        }

        return Utility.roundDouble(score);
    };
}
