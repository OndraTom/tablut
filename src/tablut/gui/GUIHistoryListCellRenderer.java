package tablut.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Vykreslovač seznamu historie.
 *
 * @author Ondřej Tom
 */
public class GUIHistoryListCellRenderer extends DefaultListCellRenderer implements ListCellRenderer<Object>
{
	/**
	 * Inkrement pro definování barev.
	 */
	private int increment;


	/**
	 * @param playerOnMove
	 */
	public GUIHistoryListCellRenderer(int playerOnMove, int selectedIndex)
	{
		super();

		increment = playerOnMove + selectedIndex;
	}


	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		// Střídáme barvy (identifikace hráče).
		if ((index + increment) % 2 == 0)
		{
			setForeground(Color.BLACK);
		}
		else
		{
			setForeground(Color.RED);
		}

		return c;
	}
}
