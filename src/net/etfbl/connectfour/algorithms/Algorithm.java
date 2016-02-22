package net.etfbl.connectfour.algorithms;

import net.etfbl.connectfour.GameBoard;
import net.etfbl.connectfour.Move;
import net.etfbl.connectfour.Game.Player;

public abstract class Algorithm {
	public abstract Move getIdealMove(GameBoard board, Player player, int depth);
	
}
