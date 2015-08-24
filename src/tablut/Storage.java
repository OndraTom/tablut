package tablut;

import tablut.exceptions.ValidatorException;
import tablut.exceptions.StorageException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Úložiště - zajišťuje ukládání/načítání hry do/ze souboru.
 *
 * @author Ondřej Tom
 */
public class Storage
{
	/**
	 * Validator načítaných dat.
	 */
	private Validator validator;


	public Storage()
	{
		validator = new Validator();
	}


	/**
	 * Vytvoří data hráče.
	 *
	 * @param playerNum
	 * @param manager
	 * @param dom
	 * @return
	 */
	private Element makePlayerElement(int playerNum, Manager manager, Document dom)
	{
		Player player	= playerNum == TablutSquare.RUSSIAN ? manager.getPlayerA() : manager.getPlayerB();
		Element element	= dom.createElement("player" + playerNum);
		Element type	= dom.createElement("type");

		element.appendChild(type);

		if (player instanceof ComputerPlayer)
		{
			type.setTextContent("computer");

			ComputerPlayer computer = (ComputerPlayer) player;

			Element difficulty = dom.createElement("difficulty");
			difficulty.setTextContent(Integer.toString(computer.getDifficulty()));

			element.appendChild(difficulty);
		}
		else
		{
			type.setTextContent("human");
		}

		return element;
	}


	/**
	 * Vytvoří data hrací desky.
	 *
	 * @param playBoard
	 * @param dom
	 * @return
	 */
	private Element makeBoardElement(PlayBoard playBoard, Document dom)
	{
		Element board = dom.createElement("board");
		Element row, col;

		int[][] boardArray = playBoard.getBoard();
		int i, j;

		for (i = 0; i < boardArray.length; i++)
		{
			row = dom.createElement("r-" + Integer.toString(i));

			for (j = 0; j < boardArray[i].length; j++)
			{
				col = dom.createElement("c-" + Integer.toString(j));
				col.setTextContent(Integer.toString(boardArray[i][j]));
				row.appendChild(col);
			}

			board.appendChild(row);
		}

		return board;
	}


	/**
	 * Vytvoří data historie.
	 *
	 * @param list
	 * @param type
	 * @param dom
	 * @return
	 */
	private Element makeHistoryListElement(List<HistoryItem> list, String type, Document dom)
	{
		Element listE = dom.createElement(type + "s");
		Element itemE, from, to, playerOnTheMove;
		for (HistoryItem item : list)
		{
			itemE = dom.createElement(type);

			from = dom.createElement("from");
			from.setAttribute("x", Integer.toString(item.getMoveFrom()[0]));
			from.setAttribute("y", Integer.toString(item.getMoveFrom()[1]));

			to = dom.createElement("to");
			to.setAttribute("x", Integer.toString(item.getMoveTo()[0]));
			to.setAttribute("y", Integer.toString(item.getMoveTo()[1]));

			playerOnTheMove = dom.createElement("playerOnTheMove");
			playerOnTheMove.setTextContent(Integer.toString(item.getPlayerOnMove()));

			itemE.appendChild(from);
			itemE.appendChild(to);
			itemE.appendChild(playerOnTheMove);
			itemE.appendChild(makeBoardElement(item.getBoard(), dom));

			listE.appendChild(itemE);
		}

		return listE;
	}


	/**
	 * Vytvoří data položky historie.
	 *
	 * @param history
	 * @param dom
	 * @return
	 */
	private Element makeHistoryElement(History history, Document dom)
	{
		Element historyE = dom.createElement("history");

		List<HistoryItem> undos = history.getUndoItems();
		List<HistoryItem> redos = history.getRedoItems();

		if (!undos.isEmpty())
		{
			historyE.appendChild(makeHistoryListElement(undos, "undo", dom));
		}

		if (!redos.isEmpty())
		{
			historyE.appendChild(makeHistoryListElement(redos, "redo", dom));
		}

		return historyE;
	}


	/**
	 * Naparsuje data.
	 *
	 * @param file
	 * @return
	 * @throws StorageException
	 */
	private Document parseXmlFile(File file) throws StorageException
	{
		DocumentBuilderFactory dfc = DocumentBuilderFactory.newInstance();

		try
		{
			Document doc = dfc.newDocumentBuilder().parse(file);

			return doc;
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			throw new StorageException("Parsing file failed: " + e.getMessage());
		}
	}


	/**
	 * Vrátí instanci hráče z dat.
	 *
	 * @param player
	 * @return
	 * @throws StorageException
	 */
	private Player makePlayerFromElement(Node player) throws StorageException
	{
		Element playerElement = (Element) player;

		try
		{
			validator.validatePlayer(playerElement);
		}
		catch (ValidatorException ve)
		{
			throw new StorageException(ve.getMessage());
		}

		String type = playerElement.getElementsByTagName("type").item(0).getTextContent().trim();

		// Human.
		if (type.equals("human"))
		{
			return new HumanPlayer();
		}

		// Computer.
		else
		{
			String difficulty = playerElement.getElementsByTagName("difficulty").item(0).getTextContent().trim();

			return new ComputerPlayer(Integer.parseInt(difficulty));
		}
	}


	/**
	 * Vrátí hráče na tahu z dat.
	 *
	 * @param playerOnMove
	 * @return
	 * @throws StorageException
	 */
	private int makePlayerOnMoveFromElement(Node playerOnMove) throws StorageException
	{
		Element playerOnMoveElement = (Element) playerOnMove;

		try
		{
			validator.validatePlayerOnMove(playerOnMoveElement);
		}
		catch (ValidatorException ve)
		{
			throw new StorageException(ve.getMessage());
		}

		return Integer.parseInt(playerOnMoveElement.getTextContent());
	}


	/**
	 * Vrátí index vítěze z dat.
	 *
	 * @param winner
	 * @return
	 * @throws StorageException
	 */
	private int makeWinnerFromElement(Node winner) throws StorageException
	{
		Element winnerElement = (Element) winner;

		try
		{
			validator.validateWinner(winnerElement);
		}
		catch (ValidatorException ve)
		{
			throw new StorageException(ve.getMessage());
		}

		return Integer.parseInt(winnerElement.getTextContent());
	}


	/**
	 * Vrátí hrací desku z dat.
	 *
	 * @param boardElement
	 * @return
	 * @throws StorageException
	 */
	private PlayBoard makeBoardFromElement(Node boardElement) throws StorageException
	{
		int i,j;
		int[][] values = new int[PlayBoard.SIZE + 1][PlayBoard.SIZE + 1];
		Element row, cell;
		Element board = (Element) boardElement;

		try
		{
			validator.validateBoard(board);
		}
		catch (ValidatorException ve)
		{
			throw new StorageException(ve.getMessage());
		}

		for (i = 0; i <= PlayBoard.SIZE; i++)
		{
			row = (Element) board.getElementsByTagName("r-" + i).item(0);

			for (j = 0; j <= PlayBoard.SIZE; j++)
			{
				cell = (Element) row.getElementsByTagName("c-" + j).item(0);

				values[i][j] = Integer.parseInt(cell.getTextContent());
			}
		}

		return new PlayBoard(values);
	}


	/**
	 * Přidá seznam historie z dat.
	 *
	 * @param type
	 * @param list
	 * @param history
	 * @throws StorageException
	 */
	private void addHistoryList(String type, NodeList list, History history) throws StorageException
	{
		int i;

		for (i = 0; i < list.getLength(); i++)
		{
			Element itemElement = (Element) list.item(i);
			Element fromElement = (Element) itemElement.getElementsByTagName("from").item(0);
			Element toElement = (Element) itemElement.getElementsByTagName("to").item(0);
			Element playerOnTheMoveElement = (Element) itemElement.getElementsByTagName("playerOnTheMove").item(0);
			Node boardElement = itemElement.getElementsByTagName("board").item(0);

			int[] from = {Integer.parseInt(fromElement.getAttribute("x")), Integer.parseInt(fromElement.getAttribute("y"))};
			int[] to = {Integer.parseInt(toElement.getAttribute("x")), Integer.parseInt(toElement.getAttribute("y"))};
			int playerOnTheMove = Integer.parseInt(playerOnTheMoveElement.getTextContent());

			HistoryItem hItem = new HistoryItem(playerOnTheMove, makeBoardFromElement(boardElement), from, to);

			if (type.equals("undo"))
			{
				history.addUndo(hItem);
			}
			else
			{
				history.addRedo(hItem);
			}
		}
	}


	/**
	 * Vytvoří historii z dat.
	 *
	 * @param historyElement
	 * @return
	 * @throws StorageException
	 */
	private History makeHistoryFromElement(Node historyElement) throws StorageException
	{
		History history = new History();
		Element hElement = (Element) historyElement;

		try
		{
			validator.validateHistory(hElement);
		}
		catch (ValidatorException ve)
		{
			throw new StorageException(ve.getMessage());
		}

		Element undos = (Element) hElement.getElementsByTagName("undos").item(0);
		Element redos = (Element) hElement.getElementsByTagName("redos").item(0);

		if (undos != null)
		{
			NodeList undo = undos.getElementsByTagName("undo");
			addHistoryList("undo", undo, history);
		}

		if (redos != null)
		{
			NodeList redo = redos.getElementsByTagName("redo");
			addHistoryList("redo", redo, history);
		}

		return history;
	}


	/**
	 * Save game.
	 *
	 * @param manager
	 * @param file
	 * @throws StorageException
	 */
	public void save(Manager manager, File file) throws StorageException
	{
		if (file == null)
		{
			throw new StorageException("Saving failed: no file set.");
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Element element;
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.newDocument();
			Element root = dom.createElement("tablut");

			root.appendChild(makePlayerElement(1, manager, dom));
			root.appendChild(makePlayerElement(2, manager, dom));

			element = dom.createElement("playerOnMove");
			element.setTextContent(Integer.toString(manager.getPlayerOnMove()));
			root.appendChild(element);

			element = dom.createElement("winner");
			element.setTextContent(Integer.toString(manager.getWinner()));
			root.appendChild(element);

			root.appendChild(makeBoardElement(manager.getPlayBoard(), dom));
			root.appendChild(makeHistoryElement(manager.getHistory(), dom));

			dom.appendChild(root);

			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(new File(file.getAbsolutePath() + ".xml"))));
		}
		catch (ParserConfigurationException | TransformerException | FileNotFoundException e)
		{
			throw new StorageException("Saving game failed: " + e.getMessage());
		}
	}


	/**
	 * Load game.
	 *
	 * @param file
	 * @return
	 * @throws StorageException
	 */
	public Manager load(File file) throws StorageException
	{
		if (file == null)
		{
			throw new StorageException("Loading failed: no file selected.");
		}

		Document doc = parseXmlFile(file);

		Player playerA = makePlayerFromElement(doc.getElementsByTagName("player1").item(0));
		Player playerB = makePlayerFromElement(doc.getElementsByTagName("player2").item(0));

		int playerOnMove = makePlayerOnMoveFromElement(doc.getElementsByTagName("playerOnMove").item(0));
		int winner = makeWinnerFromElement(doc.getElementsByTagName("winner").item(0));

		PlayBoard board = makeBoardFromElement(doc.getElementsByTagName("board").item(0));

		History history = makeHistoryFromElement(doc.getElementsByTagName("history").item(0));

		return new Manager(playerA, playerB, playerOnMove, winner, board, history);
	}
}
