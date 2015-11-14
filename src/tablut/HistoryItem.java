package tablut;

import java.util.Arrays;

/**
 * Prvek historie.
 *
 * @author Ondřej Tom
 */
public class HistoryItem
{
	/**
	 * Číslo hráče na tahu.
	 */
	private int playerOnMove;


	/**
	 * Hrací deska situace.
	 */
	private PlayBoard board;


	/**
	 * Místo, z kterého se táhlo.
	 */
	private int[] moveFrom;


	/**
	 * Místo, kam se táhlo.
	 */
	private int[] moveTo;


	/**
	 * Počet zahraných tahů, bez zajmutí.
	 */
	private int blindMovesCount;


	/**
	 * @param playerOnMove
	 * @param board
	 * @param moveFrom
	 * @param moveTo
	 */
	public HistoryItem(int playerOnMove, PlayBoard board, int[] moveFrom, int[] moveTo, int blindMovesCount)
	{
		this.playerOnMove		= playerOnMove;
		this.board				= board;
		this.moveFrom			= moveFrom;
		this.moveTo				= moveTo;
		this.blindMovesCount	= blindMovesCount;
	}


	/**
	 * Vrátí místo, ze kterého se táhlo.
	 *
	 * @return
	 */
	public int[] getMoveFrom()
	{
		return moveFrom;
	}


	/**
	 * Vrátí místo, kam se táhlo.
	 *
	 * @return
	 */
	public int[] getMoveTo()
	{
		return moveTo;
	}


	/**
	 * Vrátí číslo hráče na tahu.
	 *
	 * @return
	 */
	public int getPlayerOnMove()
	{
		return playerOnMove;
	}


	/**
	 * Vrátí hrací desku.
	 *
	 * @return
	 */
	public PlayBoard getBoard()
	{
		return board;
	}


	/**
	 * Vrátí počet zahraných tahů, bez zajmutí.
	 *
	 * @return
	 */
	public int getBlindMovesCount()
	{
		return blindMovesCount;
	}


	/**
	 * Zjistí, jestli je položka místem původu.
	 *
	 * @param item
	 * @return
	 */
	public boolean isOriginPlace(HistoryItem item)
	{
		int[] itemMoveFrom	= item.getMoveFrom();
		int[] itemMoveTo	= item.getMoveTo();

		return	Arrays.equals(itemMoveFrom, moveTo) &&
				Arrays.equals(itemMoveTo, moveFrom);
	}


	/**
	 * Vrátí text reprezentující tah (odkud kam).
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		return	TablutCoordinate.getCoordinateText(moveFrom[1], "horizontal") +
				TablutCoordinate.getCoordinateText(moveFrom[0], "vertical") +
				"  >  " +
				TablutCoordinate.getCoordinateText(moveTo[1], "horizontal") +
				TablutCoordinate.getCoordinateText(moveTo[0], "vertical");
	}
}
