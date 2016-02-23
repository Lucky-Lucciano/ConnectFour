package net.etfbl.connectfour;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.etfbl.connectfour.Game.Player;

public class GameBoard {
	private static final int EMPTY = -1;
	private static final int COLUMN_FULL = -2;
	private static final int YELLOW = 0;
	private static final int RED = 1;
	public static final int MIN_WON = -1;
	public static final int MAX_WON = 1;
	public static final int DRAW = 0;
	
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
//		//
//		System.out.println("AROOOOWW: " + this.nRows + " - " + board.length + "; this.nCols " + this.nCols);
//		System.out.println(Arrays.deepToString(board));
		this.board = new Integer[nRows][nCols];
//		this.board = board.clone();
//		try{
		for(int i = 0; i < this.nRows; i++) {
			for(int j = 0; j < this.nCols; j++) {
//				System.out.println("i: " + i + " - j: " + j + "; " + board[i][j]);
			    this.board[i][j] = board[i][j];
			}
		}
//		}catch(NullPointerException ex) {
//			System.out.println(ex.getMessage());
//		}
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
        
//        System.out.println(this);
        
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
		setPiece(findEmptyRow(column), column, player);
	}
	
	public void setPiece(int row, int column, Player player) {
//		System.out.println("Inserting piece " + player + "; ordinal: " + (Integer) player.ordinal());
		
		if(row != COLUMN_FULL && column >= 0 && column < this.nCols) {
			this.board[row][column] = (Integer) player.ordinal();
		}
    }
	
	public Integer checkTerminalState(Move previousMove, Player previousPlayer, Player playerMax) {
//		System.out.println("TERM check: " + previousMove.getRow());
		if(checkFour(previousMove.getRow(), previousMove.getColumn(), previousPlayer.ordinal())) {
//			 System.out.println("TERM prev - " + previousPlayer + " value " + (previousPlayer == Player.RED ? RED_WON : YELLOW_WON));
			return 10 * (previousPlayer == playerMax ? MAX_WON : MIN_WON);
		} else if(checkDraw(previousMove.getRow(), previousMove.getColumn(), previousPlayer.ordinal())) {
			return DRAW;
		}
		
		return null;
	}
	
	private boolean checkDraw(int row, int column, int player) {
		for(int i = 0; i < nRows; i++) {
            for(int j = 0; j < nCols; j++) {
                if(this.board[i][j] == EMPTY)
                	return false;
            }
        }
        
        return true;
    }
	
	public boolean checkFour(int row, int column, int player) {
        if(checkVertical(row, column, player))
        	return true;
        if(checkHorizontal(row, column, player))
        	return true;
        if(checkLeftDiagonal(row, column, player))
        	return true;
        if(checkRightDiagonal(row, column, player))
        	return true;
        
        return false;
    }

	private boolean checkVertical(int row, int column, int player) {
        /*
         * Ako je broj reda zadnje ubacenog elementa manja od 3, ne treba ni provjervati da li su 4 u nizu 
         */
		if(row < 3)
        	return false;
        
		/*
         * U 4 uzastopna reda elementi se moraju poklapati sa bojom igraca koji je odigrao zadnj potez, inace nije moguce da su 4 u vertikali
         */
		
        for(int i = row; i > row - 4; i--) {
        	try {
	            if(board[i][column] != player)
	            	return false;
        	} catch(ArrayIndexOutOfBoundsException ex){
    			System.out.println("checVertical ArrayBounds ex: " + ex.getMessage() + " curr row: " + i + " - nRows :" + nRows 
    					+ " - row :" + row
    					+ " - column: " + column);
    		}
        }
		
        
        return true;
    }

	private boolean  checkHorizontal(int row, int column, int player) {
		/*
         * Pocetna vrijednost je 1 jer se podrazumjeva da je trenutni igrac odigrao potez
         */
        int counter = 1;
        
        /*
         * Od trenutne pozicije broji se prvo koliko susjednih elemenata s lijeve strane pripada trenuntom igracu, zatim se na taj zbir doda i
         * broj elemenata s desne strane i ako je broj istih elemenata koji granice vec ili jedank od 4 onda su spojena 4. 
         */
        for(int i = column - 1; i >= 0; i--) {
            if(board[row][i] != player)
            	break;
            counter++;
        }
        for(int j = column + 1; j < 7; j++) {
            if(board[row][j] != player)
            	break;
            counter++;
        }
        
        return counter >= 4;
    }

	private boolean  checkLeftDiagonal(int row, int column, int player) {
        int counter = 1;
        int tmp_row = row - 1;
        int tmp_column = column - 1;
        
        /*
         * Od trenutne pozicije se prvo spustimo na po jednu kolonu i red nize pa provjeravamo da li element pripada trenutnom igracu, 
         * ako da uvecavamo brojac. Zatim provjerimo i gornje desne susjede i ako je zbirno vise od 4 u lijevoj dijagonali povezana su 4.
         */
        while(tmp_row >= 0 && tmp_column >= 0) {
            if(board[tmp_row][tmp_column] == player) {
                counter++;
                tmp_row--;
                tmp_column--;
            } else {
            	break;
            }
        }

        row += 1;
        column += 1;

        while(row < 6 && column < 7) {
            if(board[row][column] == player) {
                counter++;
                row++;
                column++;
            } else { 
            	break; 
            }
        }
        return counter >= 4;
    }

	private boolean  checkRightDiagonal(int row, int column, int player) {
        int counter = 1;
        int tmp_row = row + 1;
        int tmp_column = column - 1;

        /*
         * Od trenutne pozicije se prvo spustimo na jednu kolonu i dignemo red navise pa provjeravamo da li element pripada trenutnom igracu, 
         * ako da uvecavamo brojac. Zatim provjerimo i gornje lijeve susjede i ako je zbirno vise od 4 u lijevoj dijagonali povezana su 4.
         */
        while(tmp_row < 6 && tmp_column >= 0) {
            if(board[tmp_row][tmp_column] == player) {
                counter++;
                tmp_row++;
                tmp_column--;
            } else {
            	break;
            }
        }

        row -= 1;
        column += 1;

        while(row >= 0 && column < 7) {
            if(board[row][column] == player) {
                counter++;
                row--;
                column++;
            } else {
            	break;
            }
        }
        return counter >= 4;
    }
	
	public GameBoard actionResult(GameBoard gameBoard, Move moveToMake, Player player) {
//		System.out.println("Move (row, col): " + moveToMake.getRow() + ", " + moveToMake.getColumn());
		gameBoard.setPiece(moveToMake.getRow(), moveToMake.getColumn(), player);
		return gameBoard;
	}
	
	public List<Move> stateActions() {
		List<Move> actions = new ArrayList<Move>();
		List<Integer> freeColumns = getFreeColumns();
		int currentRow;
		
		for(Integer currentCol: freeColumns) {
			currentRow = findEmptyRow(currentCol);
			
			if(currentRow != COLUMN_FULL) {
				actions.add(new Move(currentRow, currentCol));
			} else {
				System.out.println("COLUMN FULL: " + currentCol);
			}
		}
		
		return actions;
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
