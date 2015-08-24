package tablut.listeners;

import tablut.events.LoadGameEvent;

/**
 * Rozhraní posluchače události pro načtení hry ze souboru.
 *
 * @author Ondřej Tom
 */
public interface LoadGameListener extends java.util.EventListener
{
	/**
	 * Akce po události načtení hry.
	 *
	 * @param event
	 */
	public void loadGame(LoadGameEvent event);
}
