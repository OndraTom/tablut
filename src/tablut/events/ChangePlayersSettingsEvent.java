package tablut.events;

/**
 * Událost změny nastavení hráčů.
 *
 * @author Ondřej Tom
 */
public class ChangePlayersSettingsEvent extends java.util.EventObject
{
	/**
	 * Typ hráče A.
	 */
	private int playerATypeIndex;


	/**
	 * Úroveň hráče A.
	 */
	private int playerADifficultyIndex;


	/**
	 * Typ hráče B.
	 */
	private int playerBTypeIndex;


	/**
	 * Úroveň hráče B.
	 */
	private int playerBDifficultyIndex;


	/**
	 * @param source					Objekt vyvolávající událost.
	 * @param playerATypeIndex			Typ hráče A.
	 * @param playerADifficultyIndex	Úroveň hráče A.
	 * @param playerBTypeIndex			Typ hráče B.
	 * @param playerBDifficultyIndex	Úroveň hráče B.
	 */
	public ChangePlayersSettingsEvent(Object source, int playerATypeIndex, int playerADifficultyIndex, int playerBTypeIndex, int playerBDifficultyIndex)
	{
		super(source);
		this.playerATypeIndex		= playerATypeIndex;
		this.playerADifficultyIndex = playerADifficultyIndex;
		this.playerBTypeIndex		= playerBTypeIndex;
		this.playerBDifficultyIndex = playerBDifficultyIndex;
	}


	/**
	 * Vrátí typ hráče A.
	 *
	 * @return
	 */
	public int getPlayerATypeIndex()
	{
		return playerATypeIndex;
	}


	/**
	 * Vrátí úroveň hráče A.
	 *
	 * @return
	 */
	public int getPlayerADifficultyIndex()
	{
		return playerADifficultyIndex;
	}


	/**
	 * Vrátí typ hráče B.
	 *
	 * @return
	 */
	public int getPlayerBTypeIndex()
	{
		return playerBTypeIndex;
	}


	/**
	 * Vrátí úroveň hráče B.
	 *
	 * @return
	 */
	public int getPlayerBDifficultyIndex()
	{
		return playerBDifficultyIndex;
	}
}
