package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;

public class Heuristics {
	
	/**
	 * Jednostavna eval funkcija koja broji broj mjesta gdje se 3 chipa potencijalno dodiruju (nedostaje 1 u za kombinaciju od 4)
	 * 
	 * @param board
	 * @param previousPlayer
	 * @return
	 */
	public static int stateEvaluationConnectFourSimple(GameBoard board, Player previousPlayer) {
		GameBoard currentPosition = new GameBoard(board.getBoard());

		//finds next valid move in all columns
		/*
		List<Move> nextFreeRowPosition = new ArrayList<Move>();
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition.add(x, new Move(board.findEmptyRow(x), x));
		}
		*/
		int[] nextFreeRowPosition = new int[7];
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition[x] = board.findEmptyRow(x);
		}
		//finds the heighest piece in each column touching another piece
		int[] maxColumnTouchingValues = new int[7];
		for (int x = 0; x < board.getnCols(); x++){
			int lowerElement = (x != 0 && nextFreeRowPosition[x-1] < nextFreeRowPosition[x]) ? nextFreeRowPosition[x-1] : nextFreeRowPosition[x];
			int upperElement = (x != 6 && nextFreeRowPosition[x+1] < nextFreeRowPosition[x]) ? nextFreeRowPosition[x+1] : lowerElement;
			
			maxColumnTouchingValues[x] = Math.min(0, upperElement-1);
		}

		int winningMoves = 0;
		for (int x = 0; x < board.getnCols(); x++){
			for (int y = maxColumnTouchingValues[x]; y <= nextFreeRowPosition[x] ; y++){
//				System.out.println("row: " + y + "; col: " + x);
				if(y >= 0 && x >= 0 && currentPosition.getBoard()[y][x] == -1){
					currentPosition.getBoard()[y][x] = 1;
					if(currentPosition.checkFour(y, x, 1)){
						winningMoves++;
					} 
					
					currentPosition.getBoard()[y][x] = 0;
					if (currentPosition.checkFour(y, x, 0)){
						winningMoves--;
					} 
					
					currentPosition.getBoard()[y][x] = -1;
				}
			}
		}

		return winningMoves;
		//return 0;
	}
}
