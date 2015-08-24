package tablut.listeners;

import tablut.events.ChangePlayersSettingsEvent;

/**
 * Rozhraní posluchače události změny nastavení hráčů.
 *
 * @author Ondřej Tom
 */
public interface ChangePlayersSettingsListener
{
	/**
	 * Akce provedená po události změny nastavení hráčů.
	 *
	 * @param event 
	 */
	public void changePlayersSettings(ChangePlayersSettingsEvent event);
}
