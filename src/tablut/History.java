package tablut;

import tablut.exceptions.HistoryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída reprezentuje kompletní historii hry.
 *
 * @author Ondřej Tom
 */
public class History
{
	/**
	 * Položky "undo" - směrem zpět.
	 */
	private List<HistoryItem> undoItems;


	/**
	 * Položky "redo" - směrem dopředu.
	 */
	private List<HistoryItem> redoItems;


	public History()
	{
		undoItems = new ArrayList<>();
		redoItems = new ArrayList<>();
	}


	/**
	 * Vrátí seznam položek podle typu (undo/redo).
	 *
	 * @param type
	 * @return
	 */
	private List<HistoryItem> getListByType(String type)
	{
		return type.equals("undo") ? undoItems : redoItems;
	}


	/**
	 * Zjistí, jestli undo/redo list není prázdný.
	 *
	 * @param type
	 * @return
	 */
	private boolean hasItems(String type)
	{
		// Inicializujeme požadovaný list.
		List<HistoryItem> items = getListByType(type);

		return !items.isEmpty();
	}


	/**
	 * Vrací poslední prvek z undo/redo listu.
	 *
	 * @param type
	 * @return
	 * @throws HistoryException
	 */
	private HistoryItem getItem(String type) throws HistoryException
	{
		// Inicializujeme požadovaný list.
		List<HistoryItem> items = getListByType(type);

		// Pokud je undo list prázndý, vyhodíme výjimku.
		if (!hasItems(type))
		{
			throw new HistoryException("There are no " + type + " items.");
		}

		// Zjistíme index posledního prvku v listu.
		int topIndex = items.size() - 1;

		// Vytáhneme poslední prvek.
		HistoryItem item = items.get(topIndex);

		// Smažeme poslední prvek z pole.
		items.remove(topIndex);

		// Vrátíme žádaný prvek.
		return item;
	}


	/**
	 * Přidá položku do undo/redo listu.
	 *
	 * @param type
	 * @param item
	 */
	private void addItem(String type, HistoryItem item)
	{
		getListByType(type).add(item);
	}


	/**
	 * Zjistí, jestli undo list není prázdný.
	 *
	 * @return
	 */
	public boolean hasUndo()
	{
		return hasItems("undo");
	}


	/**
	 * Zjistí, jestli redo list není prázdný.
	 *
	 * @return
	 */
	public boolean hasRedo()
	{
		return hasItems("redo");
	}


	/**
	 * Vrací všechny undo položky.
	 *
	 * @return
	 */
	public List<HistoryItem> getUndoItems()
	{
		return undoItems;
	}


	/**
	 * Vrací poslední prvek z undo listu.
	 *
	 * @return
	 * @throws HistoryException
	 */
	public HistoryItem getUndo() throws HistoryException
	{
		// Získáme prvek z listu.
		HistoryItem item = getItem("undo");

		// Předáme prvek do druhého listu.
		addItem("redo", item);

		return item;
	}


	/**
	 * Vrací všechny redo položky.
	 *
	 * @return
	 */
	public List<HistoryItem> getRedoItems()
	{
		return redoItems;
	}


	/**
	 * Vrací poslední prvek z redo listu.
	 *
	 * @return
	 * @throws HistoryException
	 */
	public HistoryItem getRedo() throws HistoryException
	{
		// Získáme prvek z listu.
		HistoryItem item = getItem("redo");

		// Předáme prvek do ruhého listu.
		addItem("undo", item);

		return item;
	}


	/**
	 * Přidá do undo listu novou položku.
	 *
	 * @param item
	 */
	public void addUndo(HistoryItem item)
	{
		addItem("undo", item);

		// Po přidání nové položky undo je třeba smazat veškeré položky redo.
		redoItems.clear();
	}


	/**
	 * Přidá do redo listu novou položku.
	 *
	 * @param item
	 */
	public void addRedo(HistoryItem item)
	{
		addItem("redo", item);
	}


	/**
	 * Vrátí n-tou položku historie - odkrokuje určitý počet undo/redo.
	 *
	 * @param index
	 * @return
	 * @throws HistoryException
	 */
	public HistoryItem getNthItem(int index) throws HistoryException
	{
		int undoItemsSize	= undoItems.size();
		String type			= "undo";
		HistoryItem item	= null;
		index++;

		// redo
		if (index > undoItemsSize)
		{
			// Musíme sejmout index o velikost pole undo.
			index -= undoItemsSize;

			// Moc velký index.
			if (index > redoItems.size())
			{
				throw new HistoryException("Index is out of the range.");
			}

			type = "redo";
		}

		// undo - musíme překlopit index
		else
		{
			index = undoItemsSize - index;
		}

		for (int i = 0; i < index; i++)
		{
			if (type.equals("undo"))
			{
				item = getUndo();
			}
			else
			{
				item = getRedo();
			}
		}

		return item;
	}
}
