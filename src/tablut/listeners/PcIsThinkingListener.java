package tablut.listeners;

/**
 * Rozhraní posluchače události pro generování nejlepšího tahu.
 *
 * @author Ondřej Tom
 */
public interface PcIsThinkingListener extends java.util.EventListener
{
	/**
	 * Voláno při zahájení generování.
	 */
	public void startThinking();


	/**
	 * Voláno při dokončení generování.
	 */
	public void stopThinking();
}
