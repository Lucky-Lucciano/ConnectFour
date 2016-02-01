package net.etfbl.connectfour;

import net.etfbl.connectfour.algorithms.AStar;
import net.etfbl.connectfour.algorithms.AlphaBetaPruning;
import net.etfbl.connectfour.algorithms.Minimax;

public class Game {
	private static final double ELEMENT_SIZE = 50;
	
	private int AIType;
	private Player player1;
	private Player player2;
	private int player1Score;
	private int player2Score;
	private Player currentPlayer;
	
	private GameBoard ConnectFourBoard;
	
	Minimax minimax;
	AlphaBetaPruning alphaBetaPrune;
	AStar astar;
	
	public enum Player {
		YELLOW, RED
	}
	
	public Game(int nRows, int nCols, int startingPlayer, int AIType) {
		ConnectFourBoard = new GameBoard(nRows, nCols);
		
		this.AIType = AIType;
		this.player1 = Player.values()[startingPlayer];
		this.player2 = getReversePlayer(player1);
		this.player1Score = 0;
		this.player2Score = 0;
		this.currentPlayer = this.player1;
		
		switch (AIType) {
			case 1:
				minimax = new Minimax();
				break;
			case 2:
				alphaBetaPrune = new AlphaBetaPruning();
				break;
			case 3:
				astar = new AStar();
				break;
			default:
				break;
		}
		
	}
	
	public Player getReversePlayer(Player player) {
        if(player.equals(Player.RED)) {
            return Player.YELLOW;
        } else {
            return Player.RED;
        }
    }

	public void makeMove(int row, int col) {
		// TODO Auto-generated method stub
		System.out.println("move");
		System.out.println("Setting piece " + currentPlayer);
		ConnectFourBoard.setPiece(row, col, currentPlayer);
		
		currentPlayer = getReversePlayer(currentPlayer);
	}
	
	/*public static void main(String[] args) {
		System.out.println(Player.RED.ordinal());
	}*/
	
	
}
