package net.etfbl.connectfour;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import net.etfbl.connectfour.Game.Player;

public class GameBoard {
	private static final int MAX_WIN_SCORE = 6;
	private static final int MIN_WIN_SCORE = 7;
	private static final int COLUMN_FULL = -2;
	
	public static final int YELLOW = 0;
	public static final int RED = 1;
	public static final int EMPTY = -1;
	public static final int MAX_WON = 1;
	public static final int MIN_WON = -1;
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
		this.board = new Integer[nRows][nCols];
		for(int i = 0; i < this.nRows; i++) {
			for(int j = 0; j < this.nCols; j++) {
//				System.out.println("i: " + i + " - j: " + j + "; " + board[i][j]);
			    this.board[i][j] = board[i][j];
			}
		}
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
        return freeColumns;
    }
	
	public boolean isBoardFull() {
    	return getFreeColumns().size() <= 0;
    }
	
	public int findEmptyRow(int column) {
        for(int i = 0; i < nRows; i++) {
            if(board[i][column] == EMPTY)
                return i;
        }
        
        return COLUMN_FULL;
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
		int currentPlayerVal = (previousPlayer == Player.YELLOW ? YELLOW : RED);
		
		// OBSOLETE: jednostavni nacin provjere connect 4 koji je dosta sporiji od trenutnog
		//if(checkFourSimple(previousMove.getRow(), previousMove.getColumn(), currentPlayerVal)) {
		if(checkFour(previousMove.getRow(), previousMove.getColumn(), currentPlayerVal)) {
			int score;
			if(previousPlayer == playerMax) {
				score = MAX_WIN_SCORE * MAX_WON;
			} else {
				/**
				 * Da bude veca kazna od pobjede
				 */
				score = MIN_WIN_SCORE * MIN_WON;
			}
			
			return score;
		} else if(checkDraw(previousMove.getRow(), previousMove.getColumn(), currentPlayerVal)) {
			return DRAW;
		}
		
		return null;
	}
	
	public boolean checkFour(int row, int column, int currentPlayerVal){
		boolean fourConnected = false;

//		if(this.board[x][y] != 0) {
			fourConnected = (fourConnected) ? fourConnected : this.checkFourConnection(currentPlayerVal, row, column, 1, 0);
			fourConnected = (fourConnected) ? fourConnected : this.checkFourConnection(currentPlayerVal, row, column, 1, -1);
			fourConnected = (fourConnected) ? fourConnected : this.checkFourConnection(currentPlayerVal, row, column, 0, 1);
			fourConnected = (fourConnected) ? fourConnected : this.checkFourConnection(currentPlayerVal, row, column, 1, 1);
//		}
			
		return fourConnected;
	}
	
	public List<Map<String, Integer>> getWinnerSequence(int row, int column, int currentPlayerVal){
		if(this.checkFourConnection(currentPlayerVal, row, column, 1, 0)) {
			return this.getWinnerConnection(currentPlayerVal, row, column, 1, 0);
		} else if(this.checkFourConnection(currentPlayerVal, row, column, 1, -1)) {
			return this.getWinnerConnection(currentPlayerVal, row, column, 1, -1);
		}else if(this.checkFourConnection(currentPlayerVal, row, column, 0, 1)) {
			return this.getWinnerConnection(currentPlayerVal, row, column, 0, 1);
		} else if(this.checkFourConnection(currentPlayerVal, row, column, 1, 1)) {
			return this.getWinnerConnection(currentPlayerVal, row, column, 1, 1);
		}
		
		return null;
	}
	
	public List<Map<String, Integer>> getWinnerConnection(int currentPlayerVal, int x, int y, int dx, int dy) {
		int length = 1;
		int i = 1;
		
		List<Map<String, Integer>> winnerSequence = new ArrayList<Map<String, Integer>>();
        Map<String, Integer> winnerMove = new HashMap<String, Integer>();
        winnerMove.put("row", x);
		winnerMove.put("col", y);
		winnerSequence.add(winnerMove);
        
		while(this.isValidMove(x + dx*i, y + dy*i)) {
			if(this.board[x + dx*i][y + dy*i] == currentPlayerVal) {
				winnerMove = new HashMap<String, Integer>();
				winnerMove.put("row", x + dx*i);
				winnerMove.put("col", y + dy*i);
				winnerSequence.add(winnerMove);
				
				length++;
				i++;
				if(length >= 4) {
					return winnerSequence;
				}
			}
			else {
				break;
			}
		}
		
		i = -1;
		while(this.isValidMove(x + dx*i,y + dy*i)) {
			if(this.board[x+ dx*i][y+ dy*i] == currentPlayerVal) {
				winnerMove = new HashMap<String, Integer>();
				winnerMove.put("row", x + dx*i);
				winnerMove.put("col", y + dy*i);
				winnerSequence.add(winnerMove);
				
				length++;
				i--;
				
				if(length >= 4) {
					return winnerSequence;
				}
			}
			else {
				break;
			}
		}

		return winnerSequence;
	}
	
	public boolean checkFourConnection(int currentPlayerVal, int x, int y, int dx, int dy) {
		int length = 1;
		int i = 1;
		
		while(this.isValidMove(x + dx*i, y + dy*i)) {
			if(this.board[x + dx*i][y + dy*i] == currentPlayerVal) {
				length++;
				i++;
			}
			else {
				break;
			}
		}
		
		i = -1;
		while(this.isValidMove(x+ dx*i,y+ dy*i)) {
			if(this.board[x+ dx*i][y+ dy*i] == currentPlayerVal) {
				length++;
				i--;
			}
			else {
				break;
			}
		}

		return (length >= 4);
	}
	
	public boolean tripleFork(Move previousMove, Player previousPlayer){
		boolean isForked = false;
		int x = previousMove.getRow();
		int y = previousMove.getColumn();
//		if(this.board[x][y] != 0) {
		
		// VERTIKALNO - kolona miruje (0) dok se redovi pomjeraju obostrano
//		isForked = (isForked) ? isForked : this.check3Fork(previousPlayer, x, y, 1, 0);
		// HORIZONTALNO - red miruje (0) dok se kolone pomjeraju obostrano
		isForked = (isForked) ? isForked : this.check3Fork(previousPlayer, x, y, 0, 1);
		// DIJAGONALA - odozgo ka dole 
		isForked = (isForked) ? isForked : this.check3Fork(previousPlayer, x, y, 1, -1);
		// DIJAGONALA - odzdo ka gore 
		isForked = (isForked) ? isForked : this.check3Fork(previousPlayer, x, y, 1, 1);
//		}
		return isForked;
	}
	
	public boolean check3Fork(Player previousPlayer, int x, int y, int dx, int dy) {
		int length = 1;
		int i = 1;
		int alphaBorder = -1;
		int omegaBorder = -1;
		int currentPlayerVal = (previousPlayer == Player.YELLOW ? YELLOW : RED);
		
		while(this.isValidMove(x + dx*i, y + dy*i)) {
			if(this.board[x + dx*i][y + dy*i] == currentPlayerVal) {
				length++;
				i++;
			}
			else {
				alphaBorder = i;
				break;
			}
		}
		
		i = -1;
		while(this.isValidMove(x+ dx*i,y+ dy*i)) {
			if(this.board[x + dx*i][y + dy*i] == currentPlayerVal) {
				length++;
				i--;
			}
			else {
				omegaBorder = i;
				break;
			}
		}

		if(length >= 3) {
			if(this.isValidMove(x + dx*alphaBorder, y + dy*alphaBorder) && this.isValidMove(x + dx*omegaBorder, y + dy*omegaBorder) &&
									this.board[x + dx*alphaBorder][y + dy*alphaBorder] == EMPTY && this.board[x + dx*omegaBorder][y + dy*omegaBorder] == EMPTY) {
				return true;
			}
		}
		
		return false;
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
	
	//returns true if the chip is on the board
	public boolean isValidMove(int row, int column) {
		return (0 <= row && row < this.nRows && 0 <= column && column < this.nCols);
	}
	
	public GameBoard actionResult(GameBoard gameBoard, Move moveToMake, Player player) {
//			System.out.println("Move (row, col): " + moveToMake.getRow() + ", " + moveToMake.getColumn());
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
	
	public boolean checkFourSimple(int row, int column, int player) {
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
