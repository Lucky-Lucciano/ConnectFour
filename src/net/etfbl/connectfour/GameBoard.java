package net.etfbl.connectfour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.etfbl.connectfour.Game.Player;

public class GameBoard {
	private static final int EMPTY = -1;
	private static final int YELLOW = 0;
	private static final int RED = 1;
	private static final int COLUMN_FULL = -2;
	
	private int nRows;
	private int nCols;
	private Integer board[][];
	
	public GameBoard(int nRows, int nCols) {
		this.nRows = nRows;
		this.nCols = nCols;
		
		this.board = new Integer[nRows][nCols];
		
		for(int i = 0; i < nRows; i++) {
            for(int j = 0; j < nCols; j++) {
                this.board[i][j] = EMPTY;
            }
        }
	}
	
	public GameBoard(Integer board[][]) {
		this.nRows = board.length;
		this.nCols = board[this.nRows -1].length;
		this.board = board.clone();
	}
	
	public Integer getPiece(int row, int col) {
    	return this.board[row][col];
    }
	
	public List<Integer> getFreeColumns() {
		List<Integer> freeColumns = new ArrayList<Integer>();

        for(int i = 0; i < nCols; i++) {
            if(this.board[nRows - 1][i] == EMPTY)
                freeColumns.add(i);
        }
        
		//System.out.println(Arrays.deepToString(this.board));
		
		//System.out.println(Arrays.toString(this.board[0]));
		//System.out.println(Arrays.toString(this.board[1]));
		
		//System.out.println(freeColumns.toString());
        
        System.out.println(this);
        
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
		System.out.println("Inserting piece " + player + "; ordinal: " + (Integer) player.ordinal());
		if(row != COLUMN_FULL && column >= 0 && column < this.nCols) {
			this.board[row][column] = (Integer) player.ordinal();
		}
	}
	
	public void setPiece(int row, int col, Player player) {
		this.board[row][col] = (Integer) player.ordinal();
    }
	
	@Override
	public String toString() {
		String boardToView = "";
		Integer currentValue = 0;
		
		for(int i = nRows - 1; i >= 0; i--) {
			boardToView += "[";
            for(int j = 0; j < nCols; j++) {
                currentValue = this.board[i][j];
                
            	if(currentValue == EMPTY) {
                	boardToView += "EMP";
                } else if(currentValue == YELLOW) {
                	boardToView += "YEL";
                } else if(currentValue == RED) {
                	boardToView += "RED";
                }
            	
            	if(j != nCols -1) {
            		boardToView += ", ";
            	}
            }
            
            boardToView += "] \n";
        }
		
		return boardToView;
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
