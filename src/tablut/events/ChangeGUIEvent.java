package tablut.events;

/**
 * Událost vytvářená při vykonaném tahu -> změna GUI.
 *
 * @author Ondřej Tom
 */
public class ChangeGUIEvent extends java.util.EventObject
{
	/**
	 * @param source
	 */
	public ChangeGUIEvent(Object source)
	{
		super(source);
	}
}
