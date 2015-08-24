package tablut.listeners;

import tablut.events.ChangeGUIEvent;

/**
 * Rozhraní pro posluchače události ChangeGUIEvent.
 *
 * @author Ondřej Tom
 */
public interface ChangeGUIListener extends java.util.EventListener
{
	/**
	 * @param event
	 */
    public void changeGUI(ChangeGUIEvent event);
}
