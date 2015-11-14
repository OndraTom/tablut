package tablut.gui;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JList;
import javax.swing.JOptionPane;
import tablut.History;
import tablut.HistoryItem;
import tablut.exceptions.HistoryException;
import tablut.listeners.HistoryListListener;

/**
 * List položek historie.
 *
 * @author Ondřej Tom
 */
public class GUIHistoryList extends JList
{
	/**
	 * Šířka listu.
	 */
	private static int LIST_WIDTH = 120;


	/**
	 * Historie.
	 */
	private History history;


	/**
	 * Seřezené položky historie redo + undo (v obráceném pořadí).
	 */
	private List<HistoryItem> historyItems;


	/**
	 * Posluchači změny položky v historii.
	 */
	private List<HistoryListListener> listeners = new ArrayList<>();


	/**
	 * @param history
	 */
	public GUIHistoryList(History history)
	{
		super();

		this.history		= history;
		this.historyItems	= getHistoryItems(history);

		setListData(this.historyItems.toArray());
		setSize();
		pointCurrentItem();
		setFocusListener();
	}


	/**
	 * Nastaví rozměry listu.
	 */
	private void setSize()
	{
		setPreferredSize(new Dimension(LIST_WIDTH, getPreferredSize().height));
	}


	/**
	 * Označí aktuální položku.
	 */
	private void pointCurrentItem()
	{
		setSelectedIndex(history.getRedoItems().size());
	}


	/**
	 * Vrátí seřezené položky historie redo + undo (v obráceném pořadí).
	 *
	 * @param history
	 * @return
	 */
	private List<HistoryItem> getHistoryItems(History history)
	{
		List<HistoryItem> historyItems = new ArrayList<>();

		// Nejprve vložíme prvky redo.
		historyItems.addAll(history.getRedoItems());

		// Prvky undo vložíme v opačném pořadí.
		List<HistoryItem> undoItems = history.getUndoItems();
		ListIterator iterator		= undoItems.listIterator(undoItems.size());
		while (iterator.hasPrevious())
		{
			historyItems.add((HistoryItem) iterator.previous());
		}

		return historyItems;
	}


	/**
	 * Nastaví akci pro kliknutí na položku historie.
	 */
	private void setFocusListener()
	{
		addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
				for (HistoryListListener listener : listeners)
				{
					try
					{
						listener.goToHistoryItem(getSelectedIndex());
					}
					catch (HistoryException ex)
					{
						JOptionPane.showMessageDialog(null, ex.getMessage());
					}
				}
			}

		}
		);
	}


	/**
	 * Přidá posluchače změny položky historie.
	 *
	 * @param listener
	 */
	public void addListener(HistoryListListener listener)
	{
		listeners.add(listener);
	}
}
