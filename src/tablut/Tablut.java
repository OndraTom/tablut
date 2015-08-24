package tablut;

import tablut.gui.GUIOptions;

/**
 * Spouštěcí soubor celé hry.
 *
 * Nakopne Options.
 *
 * @author Ondřej Tom
 */
public class Tablut
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(
			new Runnable()
			{
				@Override
				public void run()
				{
					new GUIOptions().setVisible(true);
				}
			}
		);
	}

}
