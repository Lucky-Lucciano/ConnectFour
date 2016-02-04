package net.etfbl.connectfour.algorithms;

import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;

public class Minimax {
	
	
	
	public Minimax() {
		
	}

	public static Move minimaxDecision(GameBoard currentBoardState, Player currentPlayer) {
		
		return getRandomMove(currentBoardState);
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
