package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Utility;

public class Minimax extends Algorithm{	
	Player Max;
	Player Min;
	int evalType;
	List<Integer> probabilityDistribution = Arrays.asList(1, 2, 3, 4, 3, 2, 1);
	
	public Minimax(Player maxPlayer, int type) {
		Max = maxPlayer;
		Min = Utility.oppositePlayer(maxPlayer);
		evalType = type;
	}
	
	@Override
	public Move getIdealMove(GameBoard board, int depth, int ply) {
		return minimaxDecision(board, depth);
	}
	
	public Move minimaxDecision(GameBoard board, int depth) {
		System.out.println("Starting *AlphaBeta* decision - Max: " + Max + "; Min: " + Min + "; Depth: " + depth);
		List<Move> possibleStateActions = board.stateActions();
		Move currentAction;
		Map<Integer, Integer> actionUtilities = new HashMap<Integer, Integer>();
		
		for(int i = 0; i < possibleStateActions.size(); i++) {
			currentAction = possibleStateActions.get(i);
			actionUtilities.put(i, minValue(board.actionResult(new GameBoard(board.getBoard()), currentAction, Max), currentAction, Max, depth));
		}
		
		System.out.println("Current Board : \n" + board);
		
		double max = Double.NEGATIVE_INFINITY;
        int maxKey = 0;
        List<Move> maxMoves = new ArrayList<Move>();

        for (int i = 0; i < actionUtilities.size(); i++) {
        	System.out.println("MINIMAX Result (" + i +"): " + actionUtilities.get(i) 
        		+ " MOVE: " + possibleStateActions.get(i).getRow() + "-" + possibleStateActions.get(i).getColumn());
        	if(actionUtilities.get(i) > max) {
        		max = actionUtilities.get(i);
                maxKey = i;
        	}
		}
        System.out.println("---------------------");
        /**
         * Ukoliko ima nekoliko istih rezulata Minimax-a da se izabere random neki od njih ali sa odredjenim vjerovatnocama po 'dobroti'
         */
        for (int i = 0; i < actionUtilities.size(); i++) {
        	
        	if(actionUtilities.get(i) == max) {
        		System.out.println("MINIMAX Result (" + i +"): " + actionUtilities.get(i) 
        		+ " MOVE: " + possibleStateActions.get(i).getRow() + "-" + possibleStateActions.get(i).getColumn());

                maxMoves.add(possibleStateActions.get(i));
        	}
		}
        
        List<Move> distr = new ArrayList<Move>();
        
        for (int i = 0; i < maxMoves.size(); i++) {
        	for (int j = 0; j < probabilityDistribution.get(maxMoves.get(i).getColumn()); j++) {
        		distr.add(maxMoves.get(i));
        	}
		}
        
        Move bestMove = distr.get(Utility.randomInteger(0, distr.size()));
        
        System.out.println("Best move [" + bestMove.getRow() + "][" + bestMove.getColumn() +"]");
        
        return bestMove;
	}
	
	private int maxValue(GameBoard board, Move previousMove, Player previousPlayer, int depth) {
		Integer terminalState = board.checkTerminalState(previousMove, previousPlayer, Max);
		int lowerDepth = depth - 1;
		
		if(terminalState != null) {
			return terminalState * depth;
        } else if(lowerDepth <= 0) {
        	int eval;
        	if(evalType == 1) {
        		eval = (int) Math.round(Heuristics.stateEvaluationConnectFourGaussian(new GameBoard(board.getBoard()), previousPlayer) / 10);
        	} else {
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
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, Max);
            score = Math.max(score, minValue(tempActionState, currentState, Max, lowerDepth));
        }

        return (int) score;
	}
	
	private int minValue(GameBoard board, Move previousMove, Player previousPlayer, int depth) {
		Integer terminalState = board.checkTerminalState(previousMove, previousPlayer, Max);
		int lowerDepth = depth - 1;
		
		if(terminalState != null) {
			return terminalState * depth;
        } else if(lowerDepth <= 0) {
        	int eval;
        	if(evalType == 1) { 
        		eval = (int) Math.round(Heuristics.stateEvaluationConnectFourGaussian(new GameBoard(board.getBoard()), previousPlayer) / 10);
        	} else {
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
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, Min);
            score = Math.min(score, maxValue(tempActionState, currentState, Min, lowerDepth));
        }

        return (int) score;
	}
	
	static Move getRandomMove(GameBoard board) {
		List<Integer> availableColumns = board.getFreeColumns();
        int randomColumn = availableColumns.get(Utility.randomInteger(0, availableColumns.size()));
		Move moveRandom = new Move(board.findEmptyRow(randomColumn), randomColumn);
		
        return moveRandom;
    };
    
    static Move getRandomMoveWithDistribution(GameBoard board) {
		List<Integer> availableColumns = board.getFreeColumns();
        int randomColumn = availableColumns.get(Utility.randomInteger(0, availableColumns.size()));
        
		Move moveRandom = new Move(board.findEmptyRow(randomColumn), randomColumn);
		
        return moveRandom;
    };
}
