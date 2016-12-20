package net.etfbl.connectfour;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

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
		this.plyNumber = 1;
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
		 * Prvo se vrsi provjera da li je nekome od igraca dodijeljen algoritam, ukoliko nije samo treba setovati disk na oznacenu poziciju na tabli.
		 * Ako jeste onda je trenutno na potezu AI i potrebno je sacekati njegov potez.
		 * 
		 */		
		if((currentPlayer == Player.RED ? redPlayerAlogrithm == null : yellowPlayerAlogrithm == null) && row != -1 && col != -1) {
			System.out.println("Setting piece " + currentPlayer + "; row: " + row + "; col: " + col);
			ConnectFourBoard.setPiece(row, col, currentPlayer);
			this.plyNumber++;
			
	        int gameState = checkGameCompleted(row, col);
	        this.gameActive = gameState == -1;

	        if(!this.gameActive) {
	        	System.out.println("GAME DONE - after player move! Result: " + gameState);
	        	
	        	Gson gson = new Gson();
		        Map<String, Object> resultsMap = new HashMap<String, Object>();
		        resultsMap.put("gameResult", gameState);
		        if(gameState == 0 || gameState == 1) {
		        	resultsMap.put("winnerSequence", ConnectFourBoard.getWinnerSequence(row, col, currentPlayer == Player.YELLOW ? GameBoard.YELLOW : GameBoard.RED));
		        }
		        
	        	return gson.toJson(resultsMap);
	        }
	        
			currentPlayer = Utility.oppositePlayer(currentPlayer);
		}
		
		return AIPlay();
	}
	
	public String AIPlay() {
		Gson gson = new Gson();
        Map<String, Object> resultsMap = new HashMap<String, Object>();

        // Stigao request sa klijentske strane iako je igra zavrsena.
        if(!this.gameActive) {
        	resultsMap.put("gameResult", 3);
        	
        	return gson.toJson(resultsMap);
        }
        
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
        this.plyNumber++;
        
        int gameState = checkGameCompleted(currentRow, currentColumn);

        System.out.println("End state : \n" + ConnectFourBoard);
        System.out.println("-------------------------------------------------");
        
        resultsMap.put("row", idealMove.getRow());
        resultsMap.put("column", idealMove.getColumn());
        resultsMap.put("gameResult", gameState);
        if(gameState == 0 || gameState == 1) {
        	resultsMap.put("winnerSequence", ConnectFourBoard.getWinnerSequence(currentRow, currentColumn, currentPlayer == Player.YELLOW ? GameBoard.YELLOW : GameBoard.RED));
        }
        
        currentPlayer = Utility.oppositePlayer(currentPlayer);
        
        this.gameActive = gameState == -1;

        return gson.toJson(resultsMap);
    };
    
    /**
     * Provjera da li je posljednji odigrani potez rezultirao spajanjem 4 u nizu
     * 
     * @param currentRow
     * @param currentColumn
     * @return Redni broj igraca koji je pobijedio (YELLOW = 0, RED = 1). Ako igra nije zavrsena vraca defaultnu vrijednost (-1)
     */
    private Integer checkGameCompleted(int currentRow, int currentColumn) {
    	int currentPlayerVal = (currentPlayer == Player.YELLOW ? GameBoard.YELLOW : GameBoard.RED);
    	int result;
    	
    	if(ConnectFourBoard.checkFour(currentRow, currentColumn, currentPlayerVal)) {
    		result = currentPlayerVal;
    		System.out.println("WIN! Player " + (currentPlayer == Player.YELLOW ? "YELLOW": "RED") + " has won via (" + currentRow + ", " + currentColumn + ")");
    	} else if(ConnectFourBoard.isBoardFull()) {
    		System.out.println("Draw - BOARD FULL!");
    		result = 2;
    	} else {
    		result = -1;
    	}
    	
    	return result;
    }
}
