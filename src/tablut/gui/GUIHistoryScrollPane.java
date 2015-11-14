package tablut.gui;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JScrollPane;

/**
 * Panel listu historie.
 *
 * @author Ondřej Tom
 */
public class GUIHistoryScrollPane extends JScrollPane
{
	/**
	 * Výška panelu.
	 */
	private static int PANE_HEIGHT = 285;


	/**
	 * Výška jedné položky listu.
	 */
	private static int ROW_HEIGHT = 19;


	/**
	 * Počet řádku v listu.
	 */
	private static int PANE_ITEMS_COUNT = 15;


	/**
	 * List položek.
	 */
	private GUIHistoryList list;


	/**
	 * @param list
	 */
	public GUIHistoryScrollPane(GUIHistoryList list)
	{
		super(list);

		this.list = list;

		setSize();
		setScroll();
	}


	/**
	 * Nastaví rozměry panelu.
	 */
	private void setSize()
	{
		setPreferredSize(new Dimension(getPreferredSize().width, PANE_HEIGHT));
	}


	/**
	 * Nastaví odscrollování v listu.
	 */
	private void setScroll()
	{
		if (list.getSelectedIndex() > PANE_ITEMS_COUNT - 1)
		{
			getViewport().setViewPosition(new Point(0, list.getSelectedIndex() * ROW_HEIGHT));
		}
	}
}
