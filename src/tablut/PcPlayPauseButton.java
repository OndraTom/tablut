package tablut;

import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Tlačítko pro pozastavení a spuštění hry počítače.
 *
 * @author Ondřej Tom
 */
public class PcPlayPauseButton extends JButton
{
	/**
	 * @param manager
	 */
	public PcPlayPauseButton(Manager manager)
	{
		super();

		if (manager.isGamePaused())
		{
			setText("PC - play");
		}
		else
		{
			setText("PC - pause");
		}

		addActionListener(manager);
	}
}
