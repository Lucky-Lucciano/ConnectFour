package net.etfbl.connectfour;

import java.util.ArrayList;
import java.util.List;

import net.etfbl.connectfour.Game.Player;

public class GameBoard {
	private static final int EMPTY = -1;
	private static final int COLUMN_FULL = -2;
	
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
	
	public GameBoard(Integer board[][]) {
		this.nRows = board.length;
		this.nCols = board[this.nRows - 1].length;
		System.out.println("Construct copy board " + this.nRows + " - " + this.nCols);
		this.board = board.clone();
	}
	
	public Integer getPiece(int row, int col) {
    	return this.board[row][col];
    }
	
	public List<Integer> getFreeColumns() {
		List<Integer> freeColumns = new ArrayList<Integer>();

        for(int i = 0; i < nCols; i++) {
        	System.out.println("Construct copy board " + nRows + " - " + i);
            if(board[nRows - 1][i] == EMPTY)
                freeColumns.add(i);
        }
        
        return freeColumns;
    }
	
	public int findEmptyRow(int column) {
        //if(!board[column]) return;
        for(int i = 0; i < nRows; i++) {
            if(board[i][column] == EMPTY)
                return i;
        }
        
        return COLUMN_FULL;
        
        // TODO use this mechanics instead?
        //return COLUMN_FULL;
    }
	
	public void setPiece(int column, Player player) {
		int row = findEmptyRow(column);
		
		if(row != COLUMN_FULL && column >= 0 && column < this.nCols) {
			this.board[row][column] = (Integer) player.ordinal();
		}
	}
	
	public void setPiece(int row, int col, Player player) {
		this.board[row][col] = (Integer) player.ordinal();
    }

	public int getnRows() {
		return nRows;
	}

	public void setnRows(int nRows) {
		this.nRows = nRows;
	}

	public int getnCols() {
		return nCols;
	}

	public void setnCols(int nCols) {
		this.nCols = nCols;
	}

	public Integer[][] getBoard() {
		return board;
	}

	public void setBoard(Integer[][] board) {
		this.board = board;
	}
}
