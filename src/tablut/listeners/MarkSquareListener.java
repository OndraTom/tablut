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
	 * Označí pole jako nápovědu tahu.
	 *
	 * @param x
	 * @param y
	 */
	public void markSquareAsHint(int x, int y);


	/**
	 * Odoznačení pole.
	 *
	 * @param x
	 * @param y
	 */
	public void unmarkSquare(int x, int y);
}
