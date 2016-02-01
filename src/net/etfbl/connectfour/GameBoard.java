package net.etfbl.connectfour;

import net.etfbl.connectfour.Game.Player;

public class GameBoard {
	private final int EMPTY = -1;
	
	private int nRows;
	private int nCols;
	private Integer board[][];
	
	public GameBoard(int nRows, int nCols) {
		this.nRows = nRows;
		this.nCols = nCols;
		
		this.board = new Integer[nRows + 1][nCols + 1];
		
		for(int i = 0; i < nRows; i++) {
            for(int j = 0; j < nCols; j++) {
                this.board[i][j] = EMPTY;
            }
        }
	}
	
	public Integer getPiece(int row, int col) {
    	return this.board[row][col];
    }
	
	public void setPiece(int row, int col, Player player) {
		this.board[row][col] = (Integer) player.ordinal();
    }
	
	
}
