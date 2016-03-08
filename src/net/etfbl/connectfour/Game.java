package net.etfbl.connectfour;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.algorithms.AStar;
import net.etfbl.connectfour.algorithms.Algorithm;
import net.etfbl.connectfour.algorithms.AlphaBetaPruning;
import net.etfbl.connectfour.algorithms.Minimax;

public class Game {	
	private Player player1;
	private Player player2;
	private int player1Score;
	private int player2Score;
	private Player currentPlayer;
	private boolean gameActive;
	private int depthYellow;
	private int depthRed;
	private Algorithm yellowPlayerAlogrithm;
	private Algorithm redPlayerAlogrithm;
	private boolean autoPlay;
	private GameBoard ConnectFourBoard;
	// Redni broj poteza
	private int plyNumber;
	
	public enum Player {
		YELLOW, RED
	}
	
	public Game(int nRows, int nCols, int startingPlayer, int yellowPlayerType, int redPlayerType, boolean autoplay, int depthYellow,  int depthRed) {
		this.ConnectFourBoard = new GameBoard(nRows, nCols);
		
		System.out.println("Starting new game... Yellow algo: " + yellowPlayerType + " - Red algo: " + redPlayerType);
		this.player1 = Player.values()[startingPlayer];
		this.player2 = Utility.oppositePlayer(player1);
		this.player1Score = 0;
		this.player2Score = 0;
		this.plyNumber = 0;
		this.currentPlayer = this.player1;
		this.gameActive = true;
		this.depthYellow = depthYellow;
		this.depthRed = depthRed;
		this.autoPlay = autoplay;
		
		switch(yellowPlayerType) {
			case 0:
				// HUMAN
				this.yellowPlayerAlogrithm = null;
				break;
			case 1:
				this.yellowPlayerAlogrithm = new Minimax(Player.YELLOW, 1);
				break;
			case 2:
				this.yellowPlayerAlogrithm = new Minimax(Player.YELLOW, 2);
				break;
			case 3:
				this.yellowPlayerAlogrithm = new AlphaBetaPruning(Player.YELLOW, 1);
				break;
			case 4:
				this.yellowPlayerAlogrithm = new AlphaBetaPruning(Player.YELLOW, 2);
				break;
			case 5:
				this.yellowPlayerAlogrithm = new AlphaBetaPruning(Player.YELLOW, 3);
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
				this.redPlayerAlogrithm = new Minimax(Player.RED, 1);
				break;
			case 2:
				this.redPlayerAlogrithm = new Minimax(Player.RED, 2);
				break;
			case 3:
				this.redPlayerAlogrithm = new AlphaBetaPruning(Player.RED, 1);
				break;
			case 4:
				this.redPlayerAlogrithm = new AlphaBetaPruning(Player.RED, 2);
				break;
			case 5:
				this.redPlayerAlogrithm = new AlphaBetaPruning(Player.RED, 3);
				break;
			default:
				break;
		}
	}

	public String makeMove(int row, int col) {
		/**
		 * Provjera ako nije null, znaci da je trenutno na potezu AI pa ne treba setovati piece jer se to vec radi u AIPlay
		 * 
		 * ATN: Ako je trenutni igrac HUMAN i nije prvi potez u pitanju da se registruje odigrani HUMAN potez i proslijedi dalje da AI odigra svoj
		 */		
		// TODO Zasto se smatra da poslje covjeka mora AI odigrati? Modfikovati da je moguce HUMAN vs HUMAN
		if((currentPlayer == Player.RED ? redPlayerAlogrithm == null : yellowPlayerAlogrithm == null) && row != -1 && col != -1) {
			System.out.println("Setting piece " + currentPlayer + "; row: " + row + "; col: " + col);
			ConnectFourBoard.setPiece(row, col, currentPlayer);
			currentPlayer = Utility.oppositePlayer(currentPlayer);
		}
		
		return AIPlay();
	}
	
	public String AIPlay() {
        if(!this.gameActive) {
            return null;
        }
        
        this.plyNumber++;
        
        GameBoard currentBoardState = new GameBoard(ConnectFourBoard.getBoard());
        Integer[][] testState = {
        		{1, 0, 1, 0, 0, 1, 0},
        		{1, 0, 1, 0, 0, 1, 0},
        		{0, 1, 0, 1, 1, 0, 1},
        		{1, 0, 1, 0, 0, 1, 0},
        		{0, 1, 0, 1, 1, 0, 1},
        		{1, 0, -1, -1, -1, 1, 0}
        };
        
        Move idealMove;

        if(currentPlayer == Player.YELLOW) {
        	idealMove = yellowPlayerAlogrithm.getIdealMove(new GameBoard(currentBoardState.getBoard()), this.depthYellow, this.plyNumber);
        } else {
        	idealMove = redPlayerAlogrithm.getIdealMove(new GameBoard(currentBoardState.getBoard()), this.depthRed, this.plyNumber);
        }
        
        int currentRow = idealMove.getRow();
        int currentColumn = idealMove.getColumn();

        ConnectFourBoard.setPiece(currentColumn, currentPlayer);
        System.out.println("End state : \n" + ConnectFourBoard);
        System.out.println("-------------------------------------------------");
        
        Gson gson = new Gson();
        Map<String, Integer> resultsMap = new HashMap<String, Integer>();
        resultsMap.put("row", idealMove.getRow());
        resultsMap.put("column", idealMove.getColumn());
        resultsMap.put("gameResult", checkGameCompleted(currentRow, currentColumn));
        
        currentPlayer = Utility.oppositePlayer(currentPlayer);

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
    	int currentPlayerVal = (currentPlayer == Player.YELLOW ? GameBoard.YELLOW : GameBoard.RED);
    	int result;
    	
    	if(ConnectFourBoard.checkFour(currentRow, currentColumn, currentPlayerVal)) {
    		result = currentPlayerVal;
    		System.out.println("WIN! Player " + (currentPlayer == Player.YELLOW ? "YELLOW": "RED") + " has won.");
    	} else if(ConnectFourBoard.isBoardFull()) {
    		System.out.println("Draw - BOARD FULL!");
    		result = 2;
    	} else {
    		result = -1;
    	}
    	
    	return result;
    }
}
