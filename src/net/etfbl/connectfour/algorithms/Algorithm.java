package net.etfbl.connectfour.algorithms;

import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;

public abstract class Algorithm {
	public abstract Move getIdealMove(GameBoard board, int depth, int ply);
	
}
