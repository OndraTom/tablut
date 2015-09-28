package tablut;

import tablut.exceptions.PlayerException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentace hráče.
 *
 * @author Ondřej Tom
 */
public abstract class Player
{
	/**
	 * Definice hodnoty pro jasnou výhru/prohru.
	 */
	public static int MAX = 100;


	/**
	 * Definice hodnoty pro téměř jasnou výhru/prohru.
	 */
	public static int LOT = 90;


	/**
	 * Zkontroluje, zda-li je hráč vítězem.
	 *
	 * @param playerValue
	 * @param judge
	 * @return
	 */
	private static boolean isWinner(int playerValue, Judge judge)
	{
		return ((judge.isRussian(playerValue) && judge.isKingCaptured()) || (!judge.isRussian(playerValue) && judge.isKingSave()));
	}


	/**
	 * Zkontroluje, zda-li je hráč poražen.
	 *
	 * @param playerValue
	 * @param judge
	 * @return
	 */
	private static boolean isLooser(int playerValue, Judge judge)
	{
		return !judge.isRussian(playerValue) && judge.isKingCaptured();
	}


	/**
	 * Vrátí vyhodnocení hrací desky pro danné hráče.
	 *
	 * @param playerValue
	 * @param judge
	 * @return
	 */
	private static int getBoardValue(int playerValue, Judge judge)
	{
		PlayBoard board = judge.getBoard();

		// Načte počet kamenů na hrací desce - hráče na tahu i oponenta.
		int myCount = board.getValueOnBoardCount(playerValue);
		int oponentCount = board.getValueOnBoardCount(Player.getOtherPlayer(playerValue));

		// Švédům se znásobí počet bodů x 2, protože mají 2 x méně kamenů.
		if (judge.isRussian(playerValue))
		{
			oponentCount *= 2;
		}
		else
		{
			myCount *= 2;
		}

		// Nastavíme základní vyhodnocení.
		int valuation = (myCount - oponentCount) * 5;

		// Pokud je hráč na tahu vítězem, nastavíme maximum.
		if ((judge.isRussian(playerValue) && judge.isKingSurrounded()) || (!judge.isRussian(playerValue) && judge.isKingSave()))
		{
			valuation = MAX;
		}

		return valuation;
	}


	/**
	 * Vrátí hodnotu oponenta.
	 *
	 * @param playerOnMove
	 * @return
	 */
	private static int getOtherPlayer(int playerOnMove)
	{
		return playerOnMove == TablutSquare.RUSSIAN ? TablutSquare.SWEDEN : TablutSquare.RUSSIAN;
	}


	/**
	 * @param valuation
	 * @return
	 */
	private static int further(int valuation)
	{
		if (valuation > LOT)
		{
			return valuation + 1;
		}
		else if (valuation < -LOT)
		{
			return valuation - 1;
		}

		return valuation;
	}


	/**
	 * @param valuation
	 * @return
	 */
	private static int closer(int valuation)
	{
		if (valuation > LOT)
		{
			return valuation - 1;
		}
		else if (valuation < -LOT)
		{
			return valuation + 1;
		}

		return valuation;
	}


	/**
	 * Algoritmus AlfaBeta.
	 *
	 * Vrací ohodnocení aktuálního stavu hráče na základě ořezávání alfa-beta ( http://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning ).
	 *
	 * @param judge
	 * @param playerOnMove
	 * @param deep
	 * @param alfa
	 * @param beta
	 * @return
	 */
	private static int alfabeta(Judge judge, int playerOnMove, int deep, int alfa, int beta)
	{
		// Pokud hráč na tahu zvítězil, vrátíme MAX.
		if (Player.isWinner(playerOnMove, judge))
		{
			return MAX;
		}

		// Pokud hráč na tahu prohrál, vrátíme -MAX.
		if (Player.isLooser(playerOnMove, judge))
		{
			return -MAX;
		}

		// Pokud jsme dosáhli max. hloubky, vrátíme ohodnocení desky.
		if (deep <= 0)
		{
			return Player.getBoardValue(playerOnMove, judge);
		}

		int valuation;

		// Načteme všechny možné tahy.
		List<int[][]> moves = Player.getAllPossibleMoves(judge, Player.getPlayerFields(judge, playerOnMove));

		for (int[][] move : moves)
		{
			// Zkopírujeme rozhodčího.
			Judge child = (Judge) judge.clone();

			// Zahrajeme tah.
			child.playMove(move[0], move[1], playerOnMove);

			// Zjistíme ohodnocení konkrétního tahu (do hloubky).
			valuation = -Player.alfabeta(child, Player.getOtherPlayer(playerOnMove), deep - 1, Player.further(-beta), Player.further(-alfa));
			valuation = Player.closer(valuation);

			// Pokud je ohodnocení větší, než alfa, nahradíme ji a porovnáme s betou.
			if (valuation > alfa)
			{
				alfa = valuation;

				// Ořezání.
				if (valuation >= beta)
				{
					return beta;
				}
			}
		}

		return alfa;
	}


	/**
	 * Vrátí nejlepší možný tah pro danného hráče.
	 *
	 * @param judge
	 * @param playerOnMove
	 * @param deep
	 * @return
	 * @throws PlayerException
	 */
	public int[][] getBestMove(Judge judge, int playerOnMove, int deep) throws PlayerException
	{
		// Nastavíme alfu na -MAX.
		int alfa = -MAX, valuation;

		// Načteme všechny maožné tahy hráče.
		List<int[][]> moves = Player.getAllPossibleMoves(judge, Player.getPlayerFields(judge, playerOnMove));

		// Pokud hráč nemá žádné tahy, vyhodíme výjimku.
		if (moves.isEmpty())
		{
			throw new PlayerException("Player has no moves to do. Game Over");
		}

		// Jako nejlepší tah nastavíme první.
		int[][] bestMove = moves.get(0);

		for (int[][] move : moves)
		{
			// Zkopírujeme rozhodčího.
			Judge child = (Judge) judge.clone();

			// Zahrajeme tah.
			child.playMove(move[0], move[1], playerOnMove);

			// Zjistíme ohodnocení konkrétního tahu (do hloubky).
			valuation = -Player.alfabeta(child, Player.getOtherPlayer(playerOnMove), deep, -MAX, Player.further(-alfa));
			valuation = Player.closer(valuation);

			// Pokud je ohodnocení větší, než alfa, nahradíme ji a nastavíme jako tah, jako nejlepší.
			if (valuation > alfa)
			{
				alfa = valuation;
				bestMove = move;
			}
		}

		return bestMove;
	}


	/**
	 * Načte koordináty všech kamenů danného hráče.
	 *
	 * @param judge
	 * @param playerValue
	 * @return
	 */
	private static List<int[]> getPlayerFields(Judge judge, int playerValue)
	{
		if (judge.isRussian(playerValue))
		{
			return judge.getBoard().getValuePositions(playerValue);
		}
		else
		{
			// U švédů přidá nejprve krále a pak zbylé kameny.
			List<int[]> fields = new ArrayList<>();
			fields.add(judge.getBoard().getKingsPosition());
			fields.addAll(judge.getBoard().getValuePositions(playerValue));

			return fields;
		}
	}


	/**
	 * Vrátí všechny možné tahy danného hráče.
	 *
	 * @param judge
	 * @param fields
	 * @return
	 */
	private static List<int[][]> getAllPossibleMoves(Judge judge, List<int[]> fields)
	{
		List<int[]> fieldMoves;
		List<int[][]> moves = new ArrayList<>();

		for (int[] field : fields)
		{
			fieldMoves = judge.getPossibleMoves(field);
			for (int[] fieldMove : fieldMoves)
			{
				moves.add(new int[][]{field, fieldMove});
			}
		}

		return moves;
	}
}
