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
	public static int BLIND_MOVES_MAX_COUNT		= 30;

	/**
	 * Konstanty pro měření oscilace pohybů.
	 */
	public static int MOVES_OSCILATING_LIMIT	= 4;
	public static int MOVES_OSCILATING_DIFF		= 2;


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
	 * Vrátí počet zahraných tahů, bez zajmutí.
	 *
	 * @return
	 */
	public int getBlindMovesCount()
	{
		return this.blindMovesCount;
	}


	/**
	 * Nastaví počet zahraných tahů, bez zajmutí.
	 *
	 * @param count
	 */
	public void setBlindMovesCount(int count)
	{
		this.blindMovesCount = count;
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
		return !this.isMe(playerValue, value) && value != 0 && !(isSweden(playerValue) && isKing(value));
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
	 * Kontroluje, zda-li hodnota reprezentuje švédského hráče.
	 *
	 * @param playerValue
	 * @return
	 */
	public boolean isSweden(int playerValue)
	{
		return playerValue == TablutSquare.SWEDEN;
	}


	/**
	 * Zjistí, jestli je políčko chráněné a neobsazené králem.
	 *
	 * @param coord
	 * @return
	 */
	private boolean isProtectedCapturingField(int [] coord)
	{
		return board.isProtectedField(coord) && !isKing(board.getCoordsValue(coord[0], coord[1]));
	}


	/**
	 * Zjistí, jestli je kámen obklíčen - pomocná (horizontálně).
	 *
	 * @param next
	 * @param y
	 * @param playerOnMove
	 * @return
	 */
	private boolean isCapturedWithManyHorizontal(int next, int y, int playerOnMove)
	{
		return
				(y - 1 < 0 || this.isMe(playerOnMove, board.getCoordsValue(next, y - 1)) || isProtectedCapturingField(new int[]{next, y - 1})) &&
				(y + 1 > PlayBoard.SIZE || this.isMe(playerOnMove, board.getCoordsValue(next, y + 1)) || isProtectedCapturingField(new int[]{next, y + 1}))
		;
	}


	/**
	 * Zjistí, jestli je kámen obklíčen - pomocná (vertikálně).
	 *
	 * @param x
	 * @param next
	 * @param playerOnMove
	 * @return
	 */
	private boolean isCapturedWithManyVertical(int x, int next, int playerOnMove)
	{
		return
				(x - 1 < 0 || this.isMe(playerOnMove, board.getCoordsValue(x - 1, next)) || isProtectedCapturingField(new int[]{x - 1, next})) &&
				(x + 1 > PlayBoard.SIZE || this.isMe(playerOnMove, board.getCoordsValue(x + 1, next)) || isProtectedCapturingField(new int[]{x + 1, next}))
		;
	}


	/**
	 * Zjistí, jestli je kámen obklíčen (vertikálně).
	 *
	 * @param x
	 * @param y
	 * @param direction
	 * @param playerOnMove
	 * @return
	 */
	private boolean isCaptiveVertical(int x, int y, int direction, int playerOnMove)
	{
		int next		= x + (direction * 1);
		int afterNext	= x + (direction * 2);

		if (next >= 0 && next <= PlayBoard.SIZE && !isProtectedCapturingField(new int[]{next, y}))
		{
			int suspiciousValue = board.getCoordsValue(next, y);

			if (this.isEnemy(playerOnMove, suspiciousValue))
			{
				if (afterNext >= 0 && afterNext <= PlayBoard.SIZE)
				{
					if (this.isMe(playerOnMove, board.getCoordsValue(afterNext, y)) || isProtectedCapturingField(new int[]{afterNext, y}))
					{
						if (this.isKing(suspiciousValue))
						{
							return isCapturedWithManyHorizontal(next, y, playerOnMove);
						}
						else
						{
							return true;
						}
					}
				}
				else
				{
					return isCapturedWithManyHorizontal(next, y, playerOnMove);
				}
			}
		}

		return false;
	}


	/**
	 * Zjistí, jestli je kámen obklíčen (horizontálně).
	 *
	 * @param x
	 * @param y
	 * @param direction
	 * @param playerOnMove
	 * @return
	 */
	private boolean isCaptiveHorizontal(int x, int y, int direction, int playerOnMove)
	{
		int next		= y + (direction * 1);
		int afterNext	= y + (direction * 2);

		if (next >= 0 && next <= PlayBoard.SIZE && !isProtectedCapturingField(new int[]{x, next}))
		{
			int suspiciousValue = board.getCoordsValue(x, next);

			if (this.isEnemy(playerOnMove, suspiciousValue))
			{
				if (afterNext >= 0 && afterNext <= PlayBoard.SIZE)
				{
					if (this.isMe(playerOnMove, board.getCoordsValue(x, afterNext)) || isProtectedCapturingField(new int[]{x, afterNext}))
					{
						if (this.isKing(suspiciousValue))
						{
							return isCapturedWithManyVertical(x, next, playerOnMove);
						}
						else
						{
							return true;
						}
					}
				}
				else
				{
					return isCapturedWithManyVertical(x, next, playerOnMove);
				}
			}
		}

		return false;
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
		int x = moveTo[0], y = moveTo[1];
		List<int[]> captives = new ArrayList<>();

		// Pokračujeme pouze pokud hranou figurkou není král - ten se neúčastní zajímání.
		if (!this.isKing(board.getCoordsValue(x, y)))
		{
			// Směr nahoru.
			if (isCaptiveVertical(x, y, 1, playerOnMove))
			{
				captives.add(new int[]{x + 1, y});
			}

			// Směr dolů.
			if (isCaptiveVertical(x, y, -1, playerOnMove))
			{
				captives.add(new int[]{x - 1, y});
			}

			// Směr doprava.
			if (isCaptiveHorizontal(x, y, 1, playerOnMove))
			{
				captives.add(new int[]{x, y + 1});
			}

			// Směr doleva.
			if (isCaptiveHorizontal(x, y, -1, playerOnMove))
			{
				captives.add(new int[]{x, y - 1});
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

		// Zkontroluje ortogonální pozice kolem krále.
		return
				position != null &&
				(position[0] - 1 < 0 || this.isRussian(board.getCoordsValue(position[0] - 1, position[1])) || board.isProtectedField(new int[]{position[0] - 1, position[1]})) &&
				(position[0] + 1 > PlayBoard.SIZE || this.isRussian(board.getCoordsValue(position[0] + 1, position[1])) || board.isProtectedField(new int[]{position[0] + 1, position[1]})) &&
				(position[1] - 1 < 0 || this.isRussian(board.getCoordsValue(position[0], position[1] - 1)) || board.isProtectedField(new int[]{position[0], position[1] - 1})) &&
				(position[1] + 1 > PlayBoard.SIZE || this.isRussian(board.getCoordsValue(position[0], position[1] + 1)) || board.isProtectedField(new int[]{position[0], position[1] + 1}))
		;
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
	 * Vrátí index oponenta.
	 *
	 * @param playerNumber
	 * @return
	 */
	public int getOtherPlayer(int playerNumber)
	{
		return playerNumber == TablutSquare.RUSSIAN ? TablutSquare.SWEDEN : TablutSquare.RUSSIAN;
	}


	/**
	 * Zkontroluje, zda-li je hráč vítězem.
	 *
	 * @param playerValue
	 * @return
	 */
	public boolean isPlayerWinner(int playerValue)
	{
		return ((isRussian(playerValue) && isKingCaptured()) || (isSweden(playerValue) && isKingSave()));
	}


	/**
	 * Zkontroluje, zda-li je hráč poražen.
	 *
	 * @param playerValue
	 * @return
	 */
	public boolean isPlayerLooser(int playerValue)
	{
		return isPlayerWinner(getOtherPlayer(playerValue));
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
	 * Vrátí počet zajatců daného hráče.
	 *
	 * @param player
	 * @return
	 */
	public int getPlayersCaptivesCount(int player)
	{
		if (player == TablutSquare.RUSSIAN)
		{
			return PlayBoard.SWEDES_STONES_COUNT - board.getValueOnBoardCount(TablutSquare.SWEDEN);
		}

		return PlayBoard.RUSSIANS_STONES_COUNT - board.getValueOnBoardCount(TablutSquare.RUSSIAN);
	}


	/**
	 * Zjistí, zda-li hra cyklí.
	 *
	 * Sleduje opakování pohybů v historii.
	 *
	 * @param history
	 * @return
	 */
	public boolean areMovesInCycle(History history)
	{
		List<HistoryItem> undoItems = history.getUndoItems();

		if (undoItems.size() >= MOVES_OSCILATING_LIMIT)
		{
			int top = undoItems.size() - 1;

			for (int i = top; i >= top - MOVES_OSCILATING_DIFF; i--)
			{
				if (!undoItems.get(i).isOriginPlace(undoItems.get(i - MOVES_OSCILATING_DIFF)))
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}

		return true;
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
