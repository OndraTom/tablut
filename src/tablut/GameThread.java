package tablut;

import tablut.exceptions.ManagerException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vlákno hry.
 *
 * @author Ondřej Tom
 */
public class GameThread extends Thread
{
	/**
	 * Manažer.
	 */
	private Manager manager;


	/**
	 * @param manager
	 */
	public GameThread(Manager manager)
	{
		this.manager = manager;
	}


	@Override
	public void run()
	{
		// Spustí herní smyčku.
		try
		{
			manager.startGameLoop();
		}

		// Zaloguje zachycenou výjimku.
		catch (InterruptedException | ManagerException ex)
		{
			Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
