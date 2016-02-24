package net.etfbl.connectfour;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.algorithms.AStar;
import net.etfbl.connectfour.algorithms.Algorithm;
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
	private Algorithm yellowPlayerAlogrithm;
	private Algorithm redPlayerAlogrithm;
	private boolean autoPlay;
	
	private GameBoard ConnectFourBoard;
	
	Minimax minimax;
	AlphaBetaPruning alphaBetaPrune;
	AStar astar;
	
	public enum Player {
		YELLOW, RED
	}
	
	public Game(int nRows, int nCols, int startingPlayer, int yellowPlayerType, int redPlayerType, boolean autoplay, int depth) {
		this.ConnectFourBoard = new GameBoard(nRows, nCols);
		
//		this.AIType = AIType;
		this.player1 = Player.values()[startingPlayer];
		this.player2 = getReversePlayer(player1);
		this.player1Score = 0;
		this.player2Score = 0;
		this.currentPlayer = this.player1;
		this.gameActive = true;
		this.depth = depth;
//		this.yellowPlayerType = ;
//		this.redPlayerType;
		this.autoPlay = autoplay;
		
		switch(yellowPlayerType) {
			case 0:
				// HUMAN
				this.yellowPlayerAlogrithm = null;
				break;
			case 1:
				this.yellowPlayerAlogrithm = new Minimax(1);
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
		
		switch(redPlayerType) {
			case 0:
				// HUMAN
				this.redPlayerAlogrithm = null;
				break;
			case 1:
				this.redPlayerAlogrithm = new Minimax(2);
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
		/**
		 * Provjera ako nije null, znaci da je current AI pa ne treba setovati piece jer se to vec radi u AIPlay
		 */		
		
		if((currentPlayer == Player.RED ? redPlayerAlogrithm == null : yellowPlayerAlogrithm == null) && row != -1 && col != -1) {
			System.out.println("Setting piece " + currentPlayer + "; row: " + row + "; col: " + col);
			ConnectFourBoard.setPiece(row, col, currentPlayer);
			currentPlayer = getReversePlayer(currentPlayer);
		} else {
			System.out.println("First move - player:s " + currentPlayer);
		}
		
//		if(AIType != 0) {
		return AIPlay();
//		}
		
//		return "";
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
        
//        GameBoard test = new GameBoard(hhh);
        
//        System.out.println(test);
//            depth = parseInt(document.getElementById('cutoffVal').value),
//            idealMove;

//        Minimax.setCutOffValue(isNaN(depth) ? Number.POSITIVE_INFINITY : depth);
        Move idealMove;
//        if(AIType == 'minimax') {
        if(currentPlayer == Player.YELLOW) {
        	idealMove = yellowPlayerAlogrithm.getIdealMove(new GameBoard(currentBoardState.getBoard()), currentPlayer, this.depth);
        } else {
        	idealMove = redPlayerAlogrithm.getIdealMove(new GameBoard(currentBoardState.getBoard()), currentPlayer, this.depth);
        }
        
//        } else if(AIType == 'alpha-beta') {
//            idealMove = Minimax.alphaBetaSearch(currentBoardState, currentPlayer);
//        }

//        var target = document.getElementById(idealMove.row + '_' + idealMove.col);
        // TODO voditi evidenciju o zadnjem potezu?
        
        int currentRow = idealMove.getRow();
        int currentColumn = idealMove.getColumn();

        //ConnectFourBoard.setPiece(currentRow, currentColumn, currentPlayer);
        ConnectFourBoard.setPiece(currentColumn, currentPlayer);
        System.out.println("End state : \n" + ConnectFourBoard);
        
        // TODO
        //checkGameCompleted();
        
        Gson gson = new Gson();
//        Gson gson =  new GsonBuilder().create();
//        gson.toJson("Hello");
//        gson.toJson(123);
        Map<String, Integer> resultsMap = new HashMap<String, Integer>();
        resultsMap.put("row", idealMove.getRow());
        resultsMap.put("column", idealMove.getColumn());
        resultsMap.put("gameResult", checkGameCompleted(currentRow, currentColumn));
        
        currentPlayer = getReversePlayer(currentPlayer);
//        if() {
//        	return  gson.toJson(idealMove)
//        }
//        return gson.toJson(idealMove);
        return gson.toJson(resultsMap);
    };
    
    /**
     * Provjera da li je poslednji odigrani potez rezultirao spajanjem 4 u nizu
     * 
     * @param currentRow
     * @param currentColumn
     * @return Redni broj igraca koji je pobijedio (YELLOW = 0, RED = 1) ili igra jos traje pa vrati po defaultu -1
     */
    private Integer checkGameCompleted(int currentRow, int currentColumn) {
    	int lastPlayer = (currentPlayer == Player.YELLOW ? 0 : 1);
    	int result;
    	
    	if(ConnectFourBoard.checkFour(currentRow, currentColumn, lastPlayer)) {
    		result = lastPlayer;
    	} else if(ConnectFourBoard.isBoardFull()) {
    		System.out.println("BOARD FULL!!");
    		result = 2;
    	} else {
    		result = -1;
    	}
    	
    	return result;
    }
	
	public static void main(String[] args) {
//		Game game = new Game(6, 7, 0, 1, 5);
//		
//		String move = game.AIPlay();
//		System.out.println(move);
	}
}
