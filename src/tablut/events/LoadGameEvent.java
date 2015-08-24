package tablut.events;
import tablut.Manager;

/**
 * Údálost načtení hry ze souboru.
 *
 * @author Ondřej Tom
 */
public class LoadGameEvent extends java.util.EventObject
{
	/**
	 * Manažer - nese data načítané hry.
	 */
	protected Manager manager;


	/**
	 * @param source
	 * @param manager
	 */
	public LoadGameEvent(Object source, Manager manager)
	{
		super(source);
		this.manager = manager;
	}


	/**
	 * Vrátí manažera.
	 *
	 * @return
	 */
	public Manager getManager()
	{
		return manager;
	}
}
