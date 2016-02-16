package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
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

public class Minimax {
	Player Max;
	Player Min;
	
	
	public Minimax() {
		
	}
	
	public Move minimaxDecision(GameBoard board, Player player, int depth) {
		Max = player;
		Min = getReversePlayer(player);
		
		System.out.println("Starting MINIMAX - Max: " + Max + "; Min: " + Min);
		List<Move> possibleStateActions = board.stateActions();
		Move currentAction;
		Map<Integer, Integer> actionUtilities = new HashMap<Integer, Integer>();
		
		for(int i = 0; i < possibleStateActions.size(); i++) {
			currentAction = possibleStateActions.get(i);
			actionUtilities.put(i, minValue(board.actionResult(new GameBoard(board.getBoard()), currentAction, Max), currentAction, Max, depth));
		}
		
		System.out.println("MINIMAX Result: " + actionUtilities);
		
		System.out.println("Current Board : \n" + board);
		
		double max = Double.NEGATIVE_INFINITY;
        int maxKey = 0;

        for (int i = 0; i < actionUtilities.size(); i++) {
        	System.out.println("MINIMAX Result (" + i +"): " + actionUtilities.get(i) 
        		+ " MOVE: " + possibleStateActions.get(i).getRow() + "-" + possibleStateActions.get(i).getColumn());
        	if(actionUtilities.get(i) > max) {
        		max = actionUtilities.get(i);
                maxKey = i;
        	}
		}
        /*for(var utilValue in actionUtilities) {
            if(actionUtilities[utilValue] > max) {
                max = actionUtilities[utilValue];
                maxKey = utilValue
            }
        }*/

        return possibleStateActions.get(maxKey);
		
//		return possibleStateActions.get(0);
	}
	
	private int maxValue(GameBoard board, Move previousMove, Player previousPlayer, int depth) {
		Integer terminalState = board.checkTerminalState(previousMove, previousPlayer);
		int lowerDepth = depth - 1;
		
		if(terminalState != null) {
            return terminalState;
        } else if(lowerDepth <= 0) {
        	int eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
            System.out.println("Max eval at depth: " + lowerDepth + "; state evaluation: " + eval);
            return (previousPlayer == Player.RED ? GameBoard.RED_WON : GameBoard.YELLOW_WON) * eval;
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
		Integer terminalState = board.checkTerminalState(previousMove, previousPlayer);
		int lowerDepth = depth - 1;
		
		if(terminalState != null) {
            return terminalState;
        } else if(lowerDepth <= 0) {
        	int eval = Heuristics.stateEvaluationConnectFourSimple(new GameBoard(board.getBoard()), previousPlayer);
            System.out.println("Min eval at depth: " + lowerDepth + "; state evaluation: " + eval);
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
        int randomColumn = availableColumns.get(randInt(0, availableColumns.size()));
        
		Move moveRandom = new Move(board.findEmptyRow(randomColumn), randomColumn);
		
//		Gson gson = new GsonBuilder().create();
//		Gson gson = new Gson();
//		System.out.println("Random col:" + availableColumns.get(randInt(0, availableColumns.size())));
		
        return moveRandom;
//		return null;
    };
    
    public static int randInt(int min, int max) {
        Random randGenerator = new Random();

        int randNumber = randGenerator.nextInt((max - min)) + min;

        return randNumber;
    }
    
    public Player getReversePlayer(Player player) {
        if(player.equals(Player.RED)) {
            return Player.YELLOW;
        } else {
            return Player.RED;
        }
    }
}
