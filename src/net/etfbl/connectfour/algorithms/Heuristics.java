package net.etfbl.connectfour.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.etfbl.connectfour.Game.Player;
import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Utility;

public class Heuristics {
	private static final int WIN_INCREMENT = 3;
//	private static final int LOSE_INCREMENT = 2;
	private static final int FORK_INCREMENT = 1;
	
	public static final int[][] evaluationTableConnectFour = {{3, 4, 5, 7, 5, 4, 3}, 
													          {4, 6, 8, 10, 8, 6, 4},
													          {5, 8, 11, 13, 11, 8, 5}, 
													          {5, 8, 11, 13, 11, 8, 5},
													          {4, 6, 8, 10, 8, 6, 4},
													          {3, 4, 5, 7, 5, 4, 3}};
	
	/**
	 * Jednostavna eval funkcija koja broji broj mjesta gdje se 3 chipa potencijalno dodiruju (nedostaje 1 u za kombinaciju od 4)
	 * 
	 * @param board
	 * @param previousPlayer
	 * @return
	 */
	public static int stateEvaluationConnectFourSimple(GameBoard board, Player player) {
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
		
//		System.out.println("----------------------------------------");
//		System.out.println("Starting board: \n" + board);
		
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
//			System.out.println("[" + x + "] "  + "MTV val: " + maxColumnTouchingValues[x] + "; LE: " + lowerElement + "; UE: " + upperElement);
			/*if(maxColumnTouchingValues[x] < 0) {
				System.out.println("[" + x + "] "  + "MTV val: " + maxColumnTouchingValues[x] + "; LE: " + lowerElement + "; UE: " + upperElement);
			}*/
		}
		
//		System.out.println("freePosition: " + Arrays.toString(nextFreeRowPosition) + "\n"
//						+ "MTV: " + Arrays.toString(maxColumnTouchingValues) + "\n");

		int winningMoves = 0;
		// Go trough all columns
		for (int x = 0; x < board.getnCols(); x++){
			/**
			 *  !!!!!!!!!!!!!! BUG !!!!!!!!!!!!!
			 *  Mislim da jeste jer ce ici od -1 do 0 ili 1, a treba da krene od freeColumn do vrha????
			 */
			for (int y = maxColumnTouchingValues[x]; y <= nextFreeRowPosition[x] ; y++){
//				System.out.println("[" + y + "] - [" + x + "]" + ((y >= 0 && x >= 0) ? currentPosition.getBoard()[y][x] : ""));
				/**
				 * Check if currently selected cell is empty
				 */
				if(y >= 0 && x >= 0 && currentPosition.getBoard()[y][x] == -1){
					/**
					 * Simulate RED's next move and check if he has won
					 */
					currentPosition.getBoard()[y][x] = 1;
					if(currentPosition.checkFour(y, x, 1)){
						if(player == Player.RED) {
							winningMoves++;	
						} else {
							winningMoves--;
						}
					} 
					
					/**
					 * Simulate YELLOW's next move and check if he has won
					 */
					currentPosition.getBoard()[y][x] = 0;
					if (currentPosition.checkFour(y, x, 0)){
						if(player == Player.RED) {
							winningMoves--;	
						} else {
							winningMoves++;
						}
					} 
					
					/**
					 * Reset the simulated move
					 */
					currentPosition.getBoard()[y][x] = -1;
				}
			}
		}
		
//		System.out.println("----------------------------------------");

		return winningMoves;
		//return 0;
	}
	
	public static int stateEvaluationConnectFourImproved(GameBoard board, Player player, boolean includeForkValue) {
		GameBoard currentPosition = new GameBoard(board.getBoard());
		
		//finds next valid move in all columns
		int[] nextFreeRowPosition = new int[7];
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition[x] = board.findEmptyRow(x);
		}

//		System.out.println("Starting board: \n" + board);
		
		int winningMoves = 0;
		int winningMovesRed = 0;
		int winningMovesYellow = 0;
		int lowestPosition = 0;
		// Go trough all columns
		for (int x = 0; x < board.getnCols(); x++){			
			// TODO umjesto getrows staviti red koji je min s obe strane jer nema smisla ici u visinu
			//      nije efikasno sada? ili je ipak ok....
			//		lowestPosition = 0;
			
			for (int y = nextFreeRowPosition[x]; y < board.getnRows(); y++){
				lowestPosition++;
				/**
				 * Check if currently selected cell is empty
				 * Mora biti provjera Y jer ako je red pun vraca -2
				 */
				if(y >= 0 && currentPosition.getBoard()[y][x] == -1){
					/**
					 * Simulate RED's next move and check if he has won
					 */
					// TODO dodati umjesto 1 RED value
					currentPosition.getBoard()[y][x] = 1;
					if(currentPosition.checkFour(y, x, 1)){
//						System.out.println("Red wining move [" + y + "] - [" + x + "]");
						winningMovesRed++;
						if(player == Player.RED) {
							winningMoves += WIN_INCREMENT;
						} else {
							winningMoves -= WIN_INCREMENT;
						}
					
					/**
					 * FORKING - ukoliko u trenutnom stanju board-a nema 4 u nizu, onda gledamo da li je moguca FORK situacija.
					 * Fork je stanje na boardu gdje su 3 plocice u nizu a na njihove krajeve je potencijalno moguce dodati jos jednu plocicu i tako obrazovati 4 u nizu 
					 * i tako protinvika "drzati u saci" jer moze jedan kraj sprijeciti 
					 */
					} else if(includeForkValue && lowestPosition == 1 && currentPosition.tripleFork(new Move(y, x), Player.RED)) {
//						System.out.println("====" + (lowestPosition == 1? "CUTOFFF": "") + "====== FORK ===========" + "x = " + x + " y= " + y + "=====" + Arrays.toString(nextFreeRowPosition) + "=====");
//						
//						System.out.println("End state : \n" + currentPosition);
						if(player == Player.RED) {
							winningMoves += FORK_INCREMENT;
						} else {
							winningMoves -= FORK_INCREMENT;
						}
					}
					
					/**
					 * Simulate YELLOW's next move and check if he has won
					 */
					currentPosition.getBoard()[y][x] = 0;
					if(currentPosition.checkFour(y, x, 0)){
						winningMovesYellow++;
						if(player == Player.RED) {
							winningMoves -= WIN_INCREMENT;
						} else {
							winningMoves += WIN_INCREMENT;
						}
					} else if(includeForkValue && lowestPosition == 1 && currentPosition.tripleFork(new Move(y, x), Player.YELLOW)) {
						if(player == Player.RED) {
							winningMoves -= FORK_INCREMENT;
						} else {
							winningMoves += FORK_INCREMENT;
						}
					}
					
					/**
					 * Reset the simulated move
					 */
					currentPosition.getBoard()[y][x] = -1;
				}
			}
		}
		
//		System.out.println("----- RED WIN MOVES: " + winningMovesRed + " - YELLOW WIN MOVES: " + winningMovesYellow + "---  COMBINED: " + winningMoves + "-------------------------");

		return winningMoves;
		//return 0;
	}
	
	public static int stateEvaluationConnectFourGaussian(GameBoard board, Player player) {
		GameBoard currentPosition = new GameBoard(board.getBoard());
		int[][] evaluationTable = {{3, 4, 5, 7, 5, 4, 3}, 
					               {4, 6, 8, 10, 8, 6, 4},
					               {5, 8, 11, 13, 11, 8, 5}, 
					               {5, 8, 11, 13, 11, 8, 5},
					               {4, 6, 8, 10, 8, 6, 4},
					               {3, 4, 5, 7, 5, 4, 3}};
		
		int utility = 138;
        int sum = 0;
        for (int i = 0; i < board.getnRows(); i++)
            for (int j = 0; j <board.getnCols(); j++)
                if(currentPosition.getBoard()[i][j] == 1) {
                    if(player == Player.RED) {
						sum += evaluationTable[i][j];
					} else {
						sum -= evaluationTable[i][j];
					}
                } else if(currentPosition.getBoard()[i][j] == 0) {
                    if(player == Player.RED) {
                    	sum -= evaluationTable[i][j];
					} else {
						sum += evaluationTable[i][j];
					}
                }
        
        return utility + sum;
	}
	
	// TODO Eval function na osnovu najduzih povezanih tokena 
	public static int stateEvaluationConnectFourLongestChain(GameBoard board, Player player) {
		GameBoard currentPosition = new GameBoard(board.getBoard());
		
		return 0;
	}
		
	public static Move evaluatedStartingMoveConnectFour(GameBoard board, Player player, int ply) {
		List<Move> possibleStateActions = board.stateActions();
		
		int[] nextFreeRowPosition = new int[7];
		for (int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition[x] = board.findEmptyRow(x);
		}
		
		Move idealMove = null;
		
		switch(ply) {
			case 1:
				/**
				 * Prvi potez, statisticki gledano, najbolje je odgirati u centralnoj koloni
				 */
				//TODO Postaviti distribuciju na firstMoveDistribution = Arrays.asList(0, 0, 3, 9, 3, 0, 0);
				idealMove = new Move(0, 3);
				break;
			case 2:
				/**
				 * Drugi potez, ukoliko je centralna kolona slobodna, povecaj mogucnosti njenog popunjavanja
				 */
				List<Integer> secondMoveDistribution;
				System.out.println("bbb " + nextFreeRowPosition[3]);
				if(nextFreeRowPosition[3] == 0) {
					secondMoveDistribution = Arrays.asList(0, 1, 4, 9, 4, 1, 0);
				} else {
					//TODO Korisiti distribuciju: Arrays.asList(0, 2, 6, 7, 6, 2, 0);
					
					//TODO BUG: Ovdje je 0 na sredini jer moramo biti sigurni da su centralne kolone zauzete kako nas ne bi covjek pobijedio u 4 poteza,
					//           jer ne provjeramo nista prva 4 poteza ukoliko je unevaluated state.
					secondMoveDistribution = Arrays.asList(0, 1, 3, 0, 3, 1, 0);
				}
				
				idealMove = Utility.randomlyDistributedMove(possibleStateActions, secondMoveDistribution);
				break;
			default:
				break;
		}
		
		return idealMove;
	}
	
	/**
	 * Pomocna metoda koja vraca random move zasnovan na evaluation tabeli ukoliko prvih nekoliko poteza algoritam vraca draw poziciju.
	 * U random generator ubacuje svaki moguæi potez onoliko puta koliko je specificirano tabelom evaluacije za tu kolonu. 
	 */
	public static Move evaluatedMidStartMoveConnectFour(GameBoard board, Player player, int ply) {
		List<Move> possibleStateActions = board.stateActions();
		int[] nextFreeRowPosition = new int[7];
		Move idealMove = null;
	
		for(int x = 0; x < board.getnCols(); x++){
			nextFreeRowPosition[x] = board.findEmptyRow(x);
		}
		List<Integer> lowerRowStandardDistribution = Arrays.asList(
														evaluationTableConnectFour[nextFreeRowPosition[0]][0],
														evaluationTableConnectFour[nextFreeRowPosition[1]][1],
														evaluationTableConnectFour[nextFreeRowPosition[2]][2],
														evaluationTableConnectFour[nextFreeRowPosition[3]][3],
														evaluationTableConnectFour[nextFreeRowPosition[4]][4],
														evaluationTableConnectFour[nextFreeRowPosition[5]][5],
														evaluationTableConnectFour[nextFreeRowPosition[6]][6]
													);
		
		idealMove = Utility.randomlyDistributedMove(possibleStateActions, lowerRowStandardDistribution);
		
		return idealMove;
	}
}
