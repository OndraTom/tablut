package tablut.listeners;

/**
 * Rozhraní posluchače události pro označení táhnutí.
 *
 * @author Ondřej Tom
 */
public interface MarkSquareListener extends java.util.EventListener
{
	/**
	 * Označení pole.
	 *
	 * @param x
	 * @param y
	 */
	public void markSquare(int x, int y);


	/**
	 * Odoznačení pole.
	 *
	 * @param x
	 * @param y
	 */
	public void unmarkSquare(int x, int y);
}
