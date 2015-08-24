package tablut;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentace hrací desky.
 *
 * @author Ondřej Tom
 */
public class PlayBoard implements Cloneable
{
	/**
	 * Index hranice hrací desky.
	 */
	public static int SIZE = 8;


	/**
	 * Index pole králova paláce.
	 */
	public static int KINGS_PALACE = 4;


	/**
	 * Pole králových polí.
	 */
	public static int[][] PROTECTED_FIELDS = {{0, 0}, {0, 8}, {8, 0}, {8, 8}, {4, 4}};


	/**
	 * Dvourozměrné pole hrací desky [řádek][sloupec].
	 */
	private int[][] board;


	/**
	 * Inicializuje novou hrací desku.
	 */
	public PlayBoard()
	{
		board = this.getNewPlayBoard();
	}


	/**
	 * Načte hrací desku z parametru.
	 *
	 * @param board
	 */
	public PlayBoard(int[][] board)
	{
		this.board = board;
	}


	/**
	 * Vrátí hrací desku.
	 *
	 * @return
	 */
	public int[][] getBoard()
	{
		return board;
	}


	/**
	 * Nastaví hrací desku.
	 *
	 * @param board
	 */
	public void setBoard(int[][] board)
	{
		this.board = board;
	}


	/**
	 * Vygeneruje novou hrací desku.
	 *
	 * @return
	 */
	private int[][] getNewPlayBoard()
	{
		return new int[][]{
			{0, 0, 0, 1, 1, 1, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0},
			{0, 0, 0, 0, 2, 0, 0, 0, 0},
			{1, 0, 0, 0, 2, 0, 0, 0, 1},
			{1, 1, 2, 2, 3, 2, 2, 1, 1},
			{1, 0, 0, 0, 2, 0, 0, 0, 1},
			{0, 0, 0, 0, 2, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0},
			{0, 0, 0, 1, 1, 1, 0, 0, 0}
		};
	}


	/**
	 * Zkontroluje, zda-li je pole prázdné.
	 *
	 * @param coord
	 * @return
	 */
	public boolean isCoordBlank(int[] coord)
	{
		return (board[coord[0]][coord[1]] == 0);
	}


	/**
	 * Zkontroluje, zda-li se jedná o královo pole.
	 *
	 * @param coord
	 * @return
	 */
	public boolean isProtectedField(int[] coord)
	{
		for (int[] protectedField : this.PROTECTED_FIELDS)
		{
			if (coord[0] == protectedField[0] && coord[1] == protectedField[1])
			{
				return true;
			}
		}

		return false;
	}


	/**
	 * Zkontroluje, zda-li se jedná o králův palác (pole uprostřed hracího pole).
	 *
	 * @param coord
	 * @return
	 */
	public boolean isKingsPalaceField(int[] coord)
	{
		return (coord[0] == KINGS_PALACE && coord[1] == KINGS_PALACE);
	}


	/**
	 * Smaže kámen z hrací desky.
	 *
	 * @param coord
	 */
	private void removeField(int[] coord)
	{
		board[coord[0]][coord[1]] = 0;
	}


	/**
	 * Vrátí hodnotu pole hrací desky.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getCoordsValue(int x, int y)
	{
		return board[x][y];
	}


	/**
	 * Táhne kamenem na hrací desce.
	 *
	 * @param from
	 * @param to
	 */
	public void makeMove(int[] from, int[] to)
	{
		// Načte hodnotu pole, z kterého se táhne.
		int fromValue = board[from[0]][from[1]];

		// Vynuluje pole, z kterého se táhne.
		board[from[0]][from[1]] = 0;

		// Nastaví hodnotu polen, na které se táhne.
		board[to[0]][to[1]] = fromValue;
	}


	/**
	 * Vymaže všechny zajaté kameny z hrací desky.
	 *
	 * @param captives
	 */
	public void removeCaptives(List<int[]> captives)
	{
		for (int[] captive : captives)
		{
			this.removeField(captive);
		}
	}


	/**
	 * Zkontroluje, zda-li se nachází danná hodnota na hrací desce.
	 *
	 * @param value
	 * @return
	 */
	public boolean isValueExist(int value)
	{
		int i,j;

		for (i = 0; i < board.length; i++)
		{
			for (j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == value)
				{
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * Vrátí počet výskytů danné hodnoty na hrací desce.
	 *
	 * @param value
	 * @return
	 */
	public int getValueOnBoardCount(int value)
	{
		int count = 0, i, j;

		for (i = 0; i < board.length; i++)
		{
			for (j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == value)
				{
					count++;
				}
			}
		}

		return count;
	}


	/**
	 * Vrátí pozici krále.
	 *
	 * @return
	 */
	public int[] getKingsPosition()
	{
		int i,j;

		for (i = 0; i < board.length; i++)
		{
			for (j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == TablutSquare.KING)
				{
					return new int[]{i, j};
				}
			}
		}

		return null;
	}


	/**
	 * Vrátí všechny výskyty danné hodnoty.
	 *
	 * @param value
	 * @return
	 */
	public List<int[]> getValuePositions(int value)
	{
		int i,j;
		List<int[]> positions = new ArrayList<>();

		for (i = 0; i < board.length; i++)
		{
			for (j = 0; j < board[i].length; j++)
			{
				if (board[i][j] == value)
				{
					positions.add(new int[]{i, j});
				}
			}
		}

		return positions;
	}


	/**
	 * Zkopíruje hrací desku.
	 *
	 * @return
	 */
	private int[][] cloneBoard()
	{
		int i,j;
		int[][] newBoard = new int[SIZE][SIZE];

		for (i = 0; i < board.length; i++)
		{
			for (j = 0; j < board[i].length; j++)
			{
				newBoard[i][j] = board[i][j];
			}
		}

		return newBoard;
	}


	/**
	 * Vrátí duplikát hrací desky.
	 *
	 * @return
	 */
	@Override
	public Object clone()
	{
		Cloneable duplicate = new PlayBoard(this.cloneBoard());
		return duplicate;
	}
}
