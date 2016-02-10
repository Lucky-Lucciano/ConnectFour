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
	
	public Move minimaxDecision(GameBoard board, Player player) {
		this.Max = player;
		this.Min = player;
		
		List<Move> possibleStateActions = board.stateActions();
		Move currentAction;
		Map actionUtilities = new HashMap();
		
		for(int i = 0; i < possibleStateActions.size(); i++) {
			currentAction = possibleStateActions.get(i);
			actionUtilities.put(i, minValue(board.actionResult(new GameBoard(board.getBoard()), currentAction, Max), currentAction, Max));
		}
		
		/*var max = Number.NEGATIVE_INFINITY,
                maxKey;

        for(var utilValue in actionUtilities) {
            if(actionUtilities[utilValue] > max) {
                max = actionUtilities[utilValue];
                maxKey = utilValue
            }
        }

        return possibleStateActions[maxKey];*/
		
		return getRandomMove(board);
	}
	
	private int maxValue(GameBoard board, Move previousMove, Player previousPlayer) {
		int terminalState = board.checkTerminalState(previousMove, previousPlayer);
		
		if(terminalState != -1) {
            return terminalState;
        }
		
		double score = Double.NEGATIVE_INFINITY;
		List<Move> possibleStateActions = board.stateActions();
		GameBoard tempActionState;
		Move currentState;
		
		for(int i = 0; i < possibleStateActions.size(); i++) {
			currentState = possibleStateActions.get(i);
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, Max);
            score = Math.max(score, minValue(tempActionState, currentState, Max));
        }

        return (int) score;
	}
	
	private int minValue(GameBoard board, Move previousMove, Player previousPlayer) {
		int terminalState = board.checkTerminalState(previousMove, previousPlayer);
		
		if(terminalState != -1) {
            return terminalState;
        }
		
		double score = Double.POSITIVE_INFINITY;
		List<Move> possibleStateActions = board.stateActions();
		GameBoard tempActionState;
		Move currentState;
		
		for(int i = 0; i < possibleStateActions.size(); i++) {
			currentState = possibleStateActions.get(i);
            tempActionState = board.actionResult(new GameBoard(board.getBoard()), currentState, Min);
            score = Math.min(score, maxValue(tempActionState, currentState, Min));
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
}
