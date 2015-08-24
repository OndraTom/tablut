package tablut;

/**
 * Továrna na výrobu hráče.
 *
 * @author Ondřej Tom
 */
public class PlayerFactory
{
	/**
	 * Podle zvolených parametrů vytvoří instanci hráče.
	 *
	 * @param playerIndex
	 * @param difficulty
	 * @return
	 */
	public static Player createPlayer(int playerIndex, int difficulty)
	{
		if (playerIndex == 0)
		{
			return new HumanPlayer();
		}

		return new ComputerPlayer(difficulty);
	}
}
