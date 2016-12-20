package net.etfbl.connectfour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.etfbl.connectfour.Game.Player;

public class Utility {
	
	public static Move randomlyDistributedMove(List<Move> moves, List<Integer> probabilityDistribution) {
		List<Move> distributedList = new ArrayList<Move>();
        
        for(int i = 0; i < moves.size(); i++) {
        	for(int j = 0; j < probabilityDistribution.get(moves.get(i).getColumn()); j++) {
        		distributedList.add(moves.get(i));
        	}
		}
        
        System.out.println("Distribution: " + probabilityDistribution);
        
        return distributedList.get(randomInteger(0, distributedList.size()));
	}
	
	public static Player oppositePlayer(Player player) {
        if(player.equals(Player.RED)) {
            return Player.YELLOW;
        } else {
            return Player.RED;
        }
    }
	
	public static int randomInteger(int min, int max) {
        Random randGenerator = new Random();
        
        return randGenerator.nextInt((max - min)) + min;
    }
	
	public static int roundDouble(double doubleVar) {
		return (int) Math.round(doubleVar);
	}
}
