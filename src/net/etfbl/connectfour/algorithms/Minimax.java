package net.etfbl.connectfour.algorithms;

import java.util.List;
import java.util.Random;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;

public class Minimax {
	
	
	
	public Minimax() {
		
	}

	public static JsonObject minimaxDecision(GameBoard currentBoardState, Player currentPlayer) {
		
		return getRandomMove(currentBoardState);
	}
	
	static JsonObject getRandomMove(GameBoard board) {
		List<Integer> availableColumns = board.getFreeColumns();
        
		GsonBuilder gson = new GsonBuilder();
		
		System.out.println("Random col:" + availableColumns.get(randInt(5, availableColumns.size()-1)));
		
//        return availableColumns.get(randInt(0, availableColumns.size()));
		return null;
    };
    
    public static int randInt(int min, int max) {
        Random randGenerator = new Random();

        int randNumber = randGenerator.nextInt((max - min) + 1) + min;

        return randNumber;
    }
}
