package tablut.listeners;

import tablut.exceptions.HistoryException;

/**
 * Rozhraní posluchače událostí pro práci s historií.
 *
 * @author Ondřej Tom
 */
public interface HistoryListListener
{
	/**
	 * Provede skok v historii.
	 *
	 * @param index
	 * @throws HistoryException
	 */
	public void goToHistoryItem(int index) throws HistoryException;
}