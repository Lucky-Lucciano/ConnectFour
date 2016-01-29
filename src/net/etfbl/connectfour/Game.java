package net.etfbl.connectfour;

public class Game {
	private static final double ELEMENT_SIZE = 50;
	
	private int nRows;
	private int nCols;
	private boolean vsAI;
	private Integer board[][];
	private Player player1;
	private Player player2;
	private int player1Score;
	private int player2Score;
	private Player currentPlayer;
	
	public enum Player {
		RED, YELLOW
	}
	
	public Game(int nRows, int nCols, boolean vsAI) {
		this.nRows = nRows;
		this.nCols = nCols;
		this.vsAI = vsAI;
		this.board = new Integer[nRows + 1][nCols + 1];
	}
	
	public Integer getElement(int row, int col) {
    	return this.board[row][col];
    }
	
	public Player getReversePlayer(Player player) {
        if(player.equals(Player.RED)) {
            return Player.YELLOW;
        } else {
            return Player.RED;
        }
    };
}
