package tablut;

import java.util.ArrayList;
import java.util.List;
import tablut.exceptions.PlayerException;
import tablut.listeners.PcIsThinkingListener;

/**
 * Třída reprezentující počítačového hráče.
 *
 * @author Ondřej Tom
 * @author
 */
public class ComputerPlayer extends Player
{
	/**
	 * Obtížnost.
	 */
	public int difficulty;


	/**
	 * @param difficulty
	 */
	public ComputerPlayer(int difficulty)
	{
		this.difficulty = difficulty;
	}


	/**
	 * Vrátí obtížnost poč. hráče.
	 *
	 * @return
	 */
	public int getDifficulty()
	{
		return difficulty;
	}


	/**
	 * Definice hodnoty pro jasnou výhru/prohru.
	 */
	private static int MAX = 100;


	/**
	 * Definice hodnoty pro téměř jasnou výhru/prohru.
	 */
	private static int LOT = 90;


	/**
	 * Pole posluchačů události PcIsThinkingEvent.
	 */
	private static List<PcIsThinkingListener> listeners = new ArrayList<>();


	/**
	 * Přidá posluchače pro událost generování nejlepšího tahu.
	 *
	 * @param listener
	 */
	public static void addPcIsThinkingListener(PcIsThinkingListener listener)
	{
		if (listeners.contains(listener))
		{
			return;
		}

		listeners.add(listener);
	}


	/**
	 * Obvolání posluchačů při zahájení generování nejlepšího tahu.
	 */
	private static void startThinking()
	{
		for (PcIsThinkingListener listener : listeners)
		{
			listener.startThinking();
		}
	}


	/**
	 * Obvolání posluchačů při ukončení generování nejlepšího tahu.
	 */
	private static void stopThinking()
	{
		for (PcIsThinkingListener listener : listeners)
		{
			listener.stopThinking();
		}
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
		int oponentCount = board.getValueOnBoardCount(getOtherPlayer(playerValue));

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
		if (judge.isPlayerWinner(playerOnMove))
		{
			return MAX;
		}

		// Pokud hráč na tahu prohrál, vrátíme -MAX.
		if (judge.isPlayerLooser(playerOnMove))
		{
			return -MAX;
		}

		// Pokud jsme dosáhli max. hloubky, vrátíme ohodnocení desky.
		if (deep <= 0)
		{
			return getBoardValue(playerOnMove, judge);
		}

		int valuation;

		// Načteme všechny možné tahy.
		List<int[][]> moves = getAllPossibleMoves(judge, getPlayerFields(judge, playerOnMove));

		for (int[][] move : moves)
		{
			// Zkopírujeme rozhodčího.
			Judge child = (Judge) judge.clone();

			// Zahrajeme tah.
			child.playMove(move[0], move[1], playerOnMove);

			// Zjistíme ohodnocení konkrétního tahu (do hloubky).
			valuation = -alfabeta(child, getOtherPlayer(playerOnMove), deep - 1, further(-beta), further(-alfa));
			valuation = closer(valuation);

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
	public static int[][] getBestMove(Judge judge, int playerOnMove, int deep) throws PlayerException
	{
		// Zahájení generování.
		startThinking();

		// Nastavíme alfu na -MAX.
		int alfa = -MAX, valuation;

		// Načteme všechny maožné tahy hráče.
		List<int[][]> moves = getAllPossibleMoves(judge, getPlayerFields(judge, playerOnMove));

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
			valuation = -alfabeta(child, getOtherPlayer(playerOnMove), deep, -MAX, further(-alfa));
			valuation = closer(valuation);

			// Pokud je ohodnocení větší, než alfa, nahradíme ji a nastavíme jako tah, jako nejlepší.
			if (valuation > alfa)
			{
				alfa = valuation;
				bestMove = move;
			}
		}

		// Ukončení generování.
		stopThinking();

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
