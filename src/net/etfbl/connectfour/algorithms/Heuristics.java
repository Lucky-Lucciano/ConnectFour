package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
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

		
		/*
		List<Move> nextFreeRowPosition = new ArrayList<Move>();
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition.add(x, new Move(board.findEmptyRow(x), x));
		}
		*/
		
		//finds next valid move in all columns
		int[] nextFreeRowPosition = new int[7];
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition[x] = board.findEmptyRow(x);
		}
		//finds the heighest piece in each column touching another piece
		int[] maxColumnTouchingValues = new int[7];
		
		System.out.println("----------------------------------------");
		System.out.println("Starting board: \n" + board);
		
		/**
		 * NOTE:
		 * IZBRISATI ovo sve dole i samo u proracunu uzeti pocetnu free column i krenuti ka gore?
		 * 
		 */
		for (int x = 0; x < board.getnCols(); x++){
			
			/** 
			 * Ako kolona nije prva, provjeravamo u koloni lijevo od trenutne da li je slobodno polje manje od slobodnog polja u trenutnoj koloni.
			 * Ukoliko je nize uzimamo tu vrijednost slobodnog polja u suprotnom uzimamo trenutnu vrijednost
			 */
			int lowerElement = (x != 0 && nextFreeRowPosition[x-1] < nextFreeRowPosition[x]) ? nextFreeRowPosition[x-1] : nextFreeRowPosition[x];
			
			/**
			 * Ako kolona nije poslednja, provjeravamo u koloni desno od trenutne da li je slobodno polje manje od slobodnog polja u trenutnoj koloni.
			 * Ukoliko je nize uzimamo tu vrijednost slobodnog polja u suprotnom uzimamo izracunatu vrijednost nizeg polja
			 */
			int upperElement = (x != 6 && nextFreeRowPosition[x+1] < nextFreeRowPosition[x]) ? nextFreeRowPosition[x+1] : lowerElement;
			
			/**
			 * Ako je vrijednost slobodnog elementa, umanjenog za 1, sa jedne od susjednih kolona manja od 0 onda upisujemo tu vrijednost u suprotnome 0.
			 */
			maxColumnTouchingValues[x] = Math.min(0, upperElement - 1);
			System.out.println("[" + x + "] "  + "MTV val: " + maxColumnTouchingValues[x] + "; LE: " + lowerElement + "; UE: " + upperElement);
			/*if(maxColumnTouchingValues[x] < 0) {
				System.out.println("[" + x + "] "  + "MTV val: " + maxColumnTouchingValues[x] + "; LE: " + lowerElement + "; UE: " + upperElement);
			}*/
		}
		
		System.out.println("freePosition: " + Arrays.toString(nextFreeRowPosition) + "\n"
						+ "MTV: " + Arrays.toString(maxColumnTouchingValues) + "\n");

		int winningMoves = 0;
		// Go trough all columns
		for (int x = 0; x < board.getnCols(); x++){
			/**
			 *  !!!!!!!!!!!!!! BUG !!!!!!!!!!!!!
			 *  Mislim da jeste jer ce ici od -1 do 0 ili 1, a treba da krene od freeColumn do vrha????
			 */
			for (int y = maxColumnTouchingValues[x]; y <= nextFreeRowPosition[x] ; y++){
				System.out.println("[" + y + "] - [" + x + "]" + ((y >= 0 && x >= 0) ? currentPosition.getBoard()[y][x] : ""));
				/**
				 * Check if currently selected cell is empty
				 */
				if(y >= 0 && x >= 0 && currentPosition.getBoard()[y][x] == -1){
					/**
					 * Simulate RED's next move and check if he has won
					 */
					currentPosition.getBoard()[y][x] = 1;
					if(currentPosition.checkFour(y, x, 1)){
						winningMoves++;
					} 
					
					/**
					 * Simulate YELLOW's next move and check if he has won
					 */
					currentPosition.getBoard()[y][x] = 0;
					if (currentPosition.checkFour(y, x, 0)){
						winningMoves--;
					} 
					
					/**
					 * Reset the simulated move
					 */
					currentPosition.getBoard()[y][x] = -1;
				}
			}
		}
		
		System.out.println("----------------------------------------");

		return winningMoves;
		//return 0;
	}
}
