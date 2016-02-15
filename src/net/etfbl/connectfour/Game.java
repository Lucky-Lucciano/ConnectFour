package net.etfbl.connectfour;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.algorithms.AStar;
import net.etfbl.connectfour.algorithms.AlphaBetaPruning;
import net.etfbl.connectfour.algorithms.Minimax;

public class Game {
//	private static final int DEPTH = 50;
	
	private int AIType;
	private Player player1;
	private Player player2;
	private int player1Score;
	private int player2Score;
	private Player currentPlayer;
	private boolean gameActive;
	private int depth;
	
	private GameBoard ConnectFourBoard;
	
	Minimax minimax;
	AlphaBetaPruning alphaBetaPrune;
	AStar astar;
	
	public enum Player {
		YELLOW, RED
	}
	
	public Game(int nRows, int nCols, int startingPlayer, int AIType, int depth) {
		ConnectFourBoard = new GameBoard(nRows, nCols);
		
		this.AIType = AIType;
		this.player1 = Player.values()[startingPlayer];
		this.player2 = getReversePlayer(player1);
		this.player1Score = 0;
		this.player2Score = 0;
		this.currentPlayer = this.player1;
		this.gameActive = true;
		this.depth = depth;
		
		switch(AIType) {
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

	public String makeMove(int row, int col) {
		System.out.println("Setting piece " + currentPlayer + "; row: " + row + "; col: " + col);
		ConnectFourBoard.setPiece(row, col, currentPlayer);
		currentPlayer = getReversePlayer(currentPlayer);
		if(AIType != 0) {
			String abc = AIPlay();
			return abc;
		}
		
		return "";
	}
	
	public String AIPlay() {
        if(!this.gameActive) {
            return null;
        }
        
        GameBoard currentBoardState = new GameBoard(ConnectFourBoard.getBoard());
        Integer[][] hhh = {
        		{1, 0, 1, 0, 0, 1, 0},
        		{1, 0, 1, 0, 0, 1, 0},
        		{0, 1, 0, 1, 1, 0, 1},
        		{1, 0, 1, 0, 0, 1, 0},
        		{0, 1, 0, 1, 1, 0, 1},
        		{1, 0, -1, -1, -1, 1, 0}
        };
        
        GameBoard test = new GameBoard(hhh);
        
        System.out.println(test);
//            depth = parseInt(document.getElementById('cutoffVal').value),
//            idealMove;

//        Minimax.setCutOffValue(isNaN(depth) ? Number.POSITIVE_INFINITY : depth);

//        if(AIType == 'minimax') {
        Move idealMove = minimax.minimaxDecision(new GameBoard(currentBoardState.getBoard()), currentPlayer, this.depth);
//        } else if(AIType == 'alpha-beta') {
//            idealMove = Minimax.alphaBetaSearch(currentBoardState, currentPlayer);
//        }

//        var target = document.getElementById(idealMove.row + '_' + idealMove.col);
        // TODO voditi evidenciju o zadnjem potezu?
        
        int currentRow = idealMove.getRow();
        int currentColumn = idealMove.getColumn();

        //ConnectFourBoard.setPiece(currentRow, currentColumn, currentPlayer);
        ConnectFourBoard.setPiece(currentColumn, currentPlayer);
        System.out.println("End state 1: \n" + ConnectFourBoard);
        
        // TODO
        //checkGameCompleted();
        
        Gson gson = new Gson();

        currentPlayer = getReversePlayer(currentPlayer);
        
        return gson.toJson(idealMove);
    };
	
	public static void main(String[] args) {
		Game game = new Game(6, 7, 0, 1, 5);
		
		String move = game.AIPlay();
		System.out.println(move);
	}
}
