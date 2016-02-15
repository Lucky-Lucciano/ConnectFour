package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;

public class Heuristics {
	
	/**
	 * Jedonstavna eval funkcija jer broji broj mjesta gdje se 3 chipa potencijalno dodiruju i mogucnost formiranja 4
	 * 
	 * @param board
	 * @param previousPlayer
	 * @return
	 */
	public static int stateEvaluationConnectFourSimple(GameBoard board, Player previousPlayer) {
		GameBoard currentPosition = new GameBoard(board.getBoard());

		//finds next valid move in all columns
		List<Move> maxColumnValues = new ArrayList<Move>();
		for (int x = 0; x < board.getnCols(); x++){
			maxColumnValues.add(x, new Move(board.findEmptyRow(x), x));
		}
		
		//finds the heighest piece in each column touching another piece
		List<Move> maxColumnTouchingValues = new ArrayList<Move>();
		for (int x = 0; x < board.getnCols(); x++){
			maxYHArray[x] = (x != 0 && maxYArray[x-1] < maxYArray[x]) ? maxYArray[x-1]  : maxYArray[x];
			maxYHArray[x] = (x != 7 && maxYArray[x+1] < maxYArray[x]) ? maxYArray[x+1]  : maxYHArray[x];
			maxYHArray[x] = Math.min(0, maxYHArray[x]-1);
		}

		int winningMoves = 0;
		for (int x = 0; x < board.getnCols(); x++){
			for (int y = maxYHArray[x]; y <= maxYArray[x] ; y++){
				if (cArray[x][y] == 0){
					cArray[x][y] = 1;
					if (checkGameEnd(x,y,cArray)){
						winningMoves++;
					} 
					cArray[x][y] = -1;
					if (checkGameEnd(x,y,cArray)){
						winningMoves--;
					} 
					cArray[x][y] = 0;
				}
			}
		}

		return winningMoves;
		return 0;
	}
}
