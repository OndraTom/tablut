package tablut;

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
	 * @param playerOnMove
	 * @param board
	 * @param moveFrom
	 * @param moveTo
	 */
	public HistoryItem(int playerOnMove, PlayBoard board, int[] moveFrom, int[] moveTo)
	{
		this.playerOnMove	= playerOnMove;
		this.board			= board;
		this.moveFrom		= moveFrom;
		this.moveTo			= moveTo;
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
	 * Vrátí text reprezentující tah (odkud kam).
	 * 
	 * @return
	 */
	@Override
	public String toString()
	{
		return moveFrom[0] + "|" + moveFrom[1] + " -> " + moveTo[0] + "|" + moveTo[1];
	}
}
