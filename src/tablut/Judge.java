package tablut;

import tablut.exceptions.JudgeException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rozhodčí.
 *
 * Rozhoduje o pravidlech hry.
 *
 * @author Ondřej Tom
 */
public class Judge implements Cloneable
{
	/**
	 * Maximální počet zahraných tahů, bez zajmutí.
	 */
	public static int BLIND_MOVES_MAX_COUNT = 30;


	/**
	 * Hrací deska.
	 */
	private PlayBoard board;


	/**
	 * Počet zahraných tahů, bez zajmutí.
	 */
	private int blindMovesCount = 0;


	/**
	 * @param board
	 */
	public Judge(PlayBoard board)
	{
		this.board = board;
	}


	/**
	 * Vrací hrací desku.
	 *
	 * @return
	 */
	public PlayBoard getBoard()
	{
		return board;
	}


	/**
	 * Kontroluje, zda-li byl překročen počet tahů bez zajmutí.
	 *
	 * @return
	 */
	public boolean isBlindMovesCountReached()
	{
		return this.blindMovesCount >= BLIND_MOVES_MAX_COUNT;
	}


	/**
	 * Kontroluje, zda-li je souřadnice validní.
	 *
	 * @param coord
	 * @return
	 */
	private boolean isCoordValid(int[] coord)
	{
		return (coord.length == 2 && this.isCoordRangeValid(coord[0]) && this.isCoordRangeValid(coord[1]));
	}


	/**
	 * Kontroluje, zda-li hodnota patří do rozsahu hrací desky.
	 *
	 * @param range
	 * @return
	 */
	private boolean isCoordRangeValid(int range)
	{
		return (range >= 0 && range <= PlayBoard.SIZE);
	}


	/**
	 * Vrací seznam všech možných tahů z daného místa.
	 *
	 * Při analýze polí kontroluje, zda-li je pole prázdné
	 * a není "protected" (na takové může pouze král).
	 *
	 * @param from
	 * @return
	 */
	public List<int[]> getPossibleMoves(int[] from)
	{
		// Inicializuje seznam.
		List<int[]> moves = new ArrayList<>();

		int i;
		int[] move;

		// Směr dolů.
		for (i = from[0] + 1; i <= PlayBoard.SIZE; i++)
		{
			move = new int[]{i, from[1]};
			if (!this.board.isCoordBlank(move) || (this.board.isProtectedField(move) && !this.isKing(board.getCoordsValue(from[0], from[1]))))
			{
				break;
			}
			moves.add(move);
		}

		// Směr doprava.
		for (i = from[1] + 1; i <= PlayBoard.SIZE; i++)
		{
			move = new int[]{from[0], i};
			if (!this.board.isCoordBlank(move) || (this.board.isProtectedField(move) && !this.isKing(board.getCoordsValue(from[0], from[1]))))
			{
				break;
			}
			moves.add(move);
		}

		// Směr nahoru.
		for (i = from[0] - 1; i >= 0; i--)
		{
			move = new int[]{i, from[1]};
			if (!this.board.isCoordBlank(move) || (this.board.isProtectedField(move) && !this.isKing(board.getCoordsValue(from[0], from[1]))))
			{
				break;
			}
			moves.add(move);
		}

		// Směr doleva.
		for (i = from[1] - 1; i >= 0; i--)
		{
			move = new int[]{from[0], i};
			if (!this.board.isCoordBlank(move) || (this.board.isProtectedField(move) && !this.isKing(board.getCoordsValue(from[0], from[1]))))
			{
				break;
			}
			moves.add(move);
		}

		return moves;
	}


	/**
	 * Kontroluje, zda-li je tah validní.
	 *
	 * @param from
	 * @param to
	 * @return
	 * @throws JudgeException
	 */
	public boolean isMoveValid(int[] from, int[] to) throws JudgeException
	{
		// Zkontroluje validitu souřadnic.
		if (!this.isCoordValid(from) || !this.isCoordValid(to))
		{
			return false;
		}

		// Zkontroluje, zda-li se nesnažíme táhnout prázdným políčkem.
		if (this.board.isCoordBlank(from))
		{
			throw new JudgeException("You are trying to move with blank field.");
		}

		// Zkontroluje, zda-li je tah v rozsahu možných tahů.
		for (int[] move : this.getPossibleMoves(from))
		{
			if (move[0] == to[0] && move[1] == to[1])
			{
				return true;
			}
		}

		return false;
	}


	/**
	 * Zkontroluje, zda-li hodnota patří hráči na tahu.
	 *
	 * @param playerValue	hodnota hráče na tahu
	 * @param value			kontrolovaná hodnota
	 * @return
	 */
	private boolean isMe(int playerValue, int value)
	{
		// Nekontroluje se případ krále, protože ten se neúčastní zajímání.
		return playerValue == value;
	}


	/**
	 * Zkontroluje, zda-li hodnota patří nepříteli.
	 *
	 * @param playerValue	hodnota hráče na tahu
	 * @param value			kontrolovaná hodnota
	 * @return
	 */
	private boolean isEnemy(int playerValue, int value)
	{
		// Nekontroluje se případ krále, protože ten se neúčastní zajímání.
		return !this.isMe(playerValue, value) && value != 0;
	}


	/**
	 * Kontroluje, zda-li hodnota reprezentuje krále.
	 *
	 * @param value
	 * @return
	 */
	private boolean isKing(int value)
	{
		return value == TablutSquare.KING;
	}


	/**
	 * Kontroluje, zda-li hodnota reprezentuje ruského hráče.
	 *
	 * @param playerValue
	 * @return
	 */
	public boolean isRussian(int playerValue)
	{
		return playerValue == TablutSquare.RUSSIAN;
	}


	/**
	 * Načte souřadnice všech zajímaných kamenů.
	 *
	 * @param moveTo		souřadnice tahu
	 * @param playerOnMove	hodnota hráče na tahu
	 * @return
	 */
	public List<int[]> getCaptivesCoords(int[] moveTo, int playerOnMove)
	{
		int x = moveTo[0], y = moveTo[1], suspiciousValue;
		List<int[]> captives = new ArrayList<>();

		// Pokračujeme pouze pokud hranou figurkou není král - ten se neúčastní zajímání.
		if (!this.isKing(board.getCoordsValue(x, y)))
		{
			// Směr nahoru.
			if (x - 2 >= 0)
			{
				suspiciousValue = board.getCoordsValue(x - 1, y);
				if (	// Zajímání krále.
						(this.isRussian(playerOnMove) && this.isKing(suspiciousValue)
						&& y - 1 >= 0 && y + 1 <= PlayBoard.SIZE
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 2, y)) || board.isProtectedField(new int[]{x - 2, y}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 1, y - 1)) || board.isProtectedField(new int[]{x - 1, y - 1}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 1, y + 1)) || board.isProtectedField(new int[]{x - 1, y + 1})))
						// Běžné zajímání.
						|| (this.isEnemy(playerOnMove, suspiciousValue) && !this.isKing(suspiciousValue)
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 2, y))
						|| (board.isProtectedField(new int[]{x - 2, y}) && !this.isKing(board.getCoordsValue(x - 2, y))))))
				{
					captives.add(new int[]{x - 1, y});
				}
			}

			// Směr dolů
			if (x + 2 <= PlayBoard.SIZE)
			{
				suspiciousValue = board.getCoordsValue(x + 1, y);
				if (	// Zajímání krále.
						(this.isRussian(playerOnMove) && this.isKing(suspiciousValue)
						&& y - 1 >= 0 && y + 1 <= PlayBoard.SIZE
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 2, y)) || board.isProtectedField(new int[]{x + 2, y}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 1, y - 1)) || board.isProtectedField(new int[]{x + 1, y - 1}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 1, y + 1)) || board.isProtectedField(new int[]{x + 1, y + 1})))
						// Běžné zajímání.
						|| (this.isEnemy(playerOnMove, suspiciousValue) && !this.isKing(suspiciousValue)
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 2, y))
						|| (board.isProtectedField(new int[]{x + 2, y}) && !this.isKing(board.getCoordsValue(x + 2, y))))))
				{
					captives.add(new int[]{x + 1, y});
				}
			}

			// Směr doprava.
			if (y + 2 <= PlayBoard.SIZE)
			{
				suspiciousValue = board.getCoordsValue(x, y + 1);
				if (	// Zajímání krále.
						(this.isRussian(playerOnMove) && this.isKing(suspiciousValue)
						&& x - 1 >= 0 && x + 1 <= PlayBoard.SIZE
						&& (this.isMe(playerOnMove, board.getCoordsValue(x, y + 2)) || board.isProtectedField(new int[]{x, y + 2}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 1, y + 1)) || board.isProtectedField(new int[]{x - 1, y + 1}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 1, y + 1)) || board.isProtectedField(new int[]{x + 1, y + 1})))
						// Běžné zajímání.
						|| (this.isEnemy(playerOnMove, suspiciousValue) && !this.isKing(suspiciousValue)
						&& (this.isMe(playerOnMove, board.getCoordsValue(x, y + 2))
						|| (board.isProtectedField(new int[]{x, y + 2}) && !this.isKing(board.getCoordsValue(x, y + 2))))))
				{
					captives.add(new int[]{x, y + 1});
				}
			}

			// Směr doleva.
			if (y - 2 >= 0)
			{
				suspiciousValue = board.getCoordsValue(x, y - 1);
				if (	// Zajímání krále.
						(this.isRussian(playerOnMove) && this.isKing(suspiciousValue)
						&& x - 1 >= 0 && x + 1 <= PlayBoard.SIZE
						&& (this.isMe(playerOnMove, board.getCoordsValue(x, y - 2)) || board.isProtectedField(new int[]{x, y - 2}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x - 1, y - 1)) || board.isProtectedField(new int[]{x - 1, y - 1}))
						&& (this.isMe(playerOnMove, board.getCoordsValue(x + 1, y - 1)) || board.isProtectedField(new int[]{x + 1, y - 1})))
						// Běžné zajímání.
						|| (this.isEnemy(playerOnMove, suspiciousValue) && !this.isKing(suspiciousValue)
						&& (this.isMe(playerOnMove, board.getCoordsValue(x, y - 2))
						|| (board.isProtectedField(new int[]{x, y - 2}) && !this.isKing(board.getCoordsValue(x, y - 2))))))
				{
					captives.add(new int[]{x, y - 1});
				}
			}
		}

		return captives;
	}



	/**
	 * Zkontroluje, zda-li byl král zajat.
	 *
	 * @return
	 */
	public boolean isKingCaptured()
	{
		return !board.isValueExist(TablutSquare.KING);
	}


	/**
	 * Zkontroluje, zda-li je král obklíčen.
	 *
	 * @return
	 */
	public boolean isKingSurrounded()
	{
		// Načte pozici krále.
		int[] position = board.getKingsPosition();

		// Zkontroluje ortogonální pozice kolem krále (zda-li se jedná o nepřítele).
		return position != null && position[0] - 1 >= 0 && position[0] + 1 <= PlayBoard.SIZE && position[1] - 1 >= 0 && position[1] + 1 <= PlayBoard.SIZE
				&& this.isRussian(board.getCoordsValue(position[0] - 1, position[1]))
				&& this.isRussian(board.getCoordsValue(position[0] + 1, position[1]))
				&& this.isRussian(board.getCoordsValue(position[0], position[1] - 1))
				&& this.isRussian(board.getCoordsValue(position[0], position[1] + 1));
	}


	/**
	 * Zkontroluje, zda-li král nestojí na "protected" poli.
	 *
	 * @return
	 */
	public boolean isKingSave()
	{
		for (int[] pField : this.board.PROTECTED_FIELDS)
		{
			// Pokud se nejedná o králův palác (pole uprostřed hrací desky) a král na něm stojí, hra končí.
			if (!board.isKingsPalaceField(pField) && this.isKing(board.getCoordsValue(pField[0], pField[1])))
			{
				return true;
			}
		}

		return false;
	}


	/**
	 * Zahraje tah.
	 *
	 * @param from
	 * @param to
	 * @param playerOnMove
	 */
	public void playMove(int[] from, int[] to, int playerOnMove)
	{
		// Provede tah na desce.
		board.makeMove(from, to);

		// Načte pozice zajatých kamenů.
		List<int[]> captives = this.getCaptivesCoords(to, playerOnMove);

		// Pokud jsou nalezeny kameny pro zajetí, vymažeme z desky a vynulujeme počet tahů, bez odebrání kamene.
		if (!captives.isEmpty())
		{
			this.blindMovesCount = 0;
			board.removeCaptives(captives);
		}
		// Pokud se kameny nezajímají, inkrementujeme počet tahů, bez odebrání kamene.
		else
		{
			this.blindMovesCount++;
		}
	}


	/**
	 * Při kopii celého objektu je třeba vytvořit kopii hrací desky.
	 *
	 * @return
	 */
	@Override
	public Object clone()
	{
		Cloneable duplicate = new Judge((PlayBoard) board.clone());
		return duplicate;
	}


	/**
	 * Vytváření kopie s předáním hrací desky.
	 *
	 * @param board
	 * @return
	 */
	public Object clone(PlayBoard board)
	{
		Cloneable duplicate = new Judge(board);
		return duplicate;
	}
}
