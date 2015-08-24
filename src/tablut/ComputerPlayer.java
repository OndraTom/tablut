package tablut;

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
}
