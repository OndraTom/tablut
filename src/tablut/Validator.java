package tablut;

import tablut.exceptions.ValidatorException;
import org.w3c.dom.*;

/**
 * Validuje data při načítání hry.
 *
 * @author Ondřej Tom
 */
public class Validator
{
	/**
	 * Validuje hráče.
	 *
	 * @param player
	 * @throws ValidatorException
	 */
	public void validatePlayer(Element player) throws ValidatorException
	{
		if (player == null)
		{
			throw new ValidatorException("One or both of the players are not defined.");
		}

		Element type = (Element) player.getElementsByTagName("type").item(0);
		if (type == null || !type.getNodeName().trim().equals("type"))
		{
			throw new ValidatorException("Player has not defined type.");
		}

		String typeValue = type.getTextContent().trim();

		if (!typeValue.equals("human") && !typeValue.equals("computer"))
		{
			throw new ValidatorException("Player has unknown type.");
		}

		if (typeValue.equals("computer"))
		{
			Element difficulty = (Element) player.getElementsByTagName("difficulty").item(0);
			if (difficulty == null || !difficulty.getNodeName().trim().equals("difficulty"))
			{
				throw new ValidatorException("Player has not defined difficulty.");
			}

			int difficultyValue = Integer.parseInt(difficulty.getTextContent().trim());

			if (difficultyValue < 0 || difficultyValue > 3)
			{
				throw new ValidatorException("Difficulty of computer player has to be in interval between 0 and 3.");
			}
		}
	}


	/**
	 * Ověří, zda-li je hodnota platná pro definici hráče.
	 *
	 * @param value
	 * @return
	 */
	private boolean isPlayerValue(int value)
	{
		return value == TablutSquare.RUSSIAN || value == TablutSquare.SWEDEN;
	}


	/**
	 * Validuje hráče na tahu.
	 *
	 * @param playerOnMove
	 * @throws ValidatorException
	 */
	public void validatePlayerOnMove(Element playerOnMove) throws ValidatorException
	{
		if (playerOnMove == null)
		{
			throw new ValidatorException("Player on move is not defined.");
		}

		int val = Integer.parseInt(playerOnMove.getTextContent());

		if (!isPlayerValue(val))
		{
			throw new ValidatorException("Player on move has bad value.");
		}
	}


	/**
	 * Validuje vítěze.
	 *
	 * @param winner
	 * @throws ValidatorException
	 */
	public void validateWinner(Element winner) throws ValidatorException
	{
		if (winner == null)
		{
			throw new ValidatorException("Winner is not defined");
		}

		int val = Integer.parseInt(winner.getTextContent());

		if (val != 0 && !isPlayerValue(val))
		{
			throw new ValidatorException("Winner has bad value.");
		}
	}


	/**
	 * Validuje hrací desku.
	 *
	 * @param board
	 * @throws ValidatorException
	 */
	public void validateBoard(Element board) throws ValidatorException
	{
		int i,j,val;
		Element row, cell;

		if (board == null)
		{
			throw new ValidatorException("Board is not defined");
		}

		for (i = 0; i <= PlayBoard.SIZE; i++)
		{
			row = (Element) board.getElementsByTagName("r-" + i).item(0);

			if (row == null)
			{
				throw new ValidatorException("Board row is missing.");
			}

			for (j = 0; j <= PlayBoard.SIZE; j++)
			{
				cell = (Element) row.getElementsByTagName("c-" + j).item(0);

				if (cell == null)
				{
					throw new ValidatorException("Board cell is missing.");
				}

				val = Integer.parseInt(cell.getTextContent());

				if (val < 0 || val > PlayBoard.SIZE)
				{
					throw new ValidatorException("Board value is out of bounds.");
				}
			}
		}
	}


	/**
	 * Validuje koordinátu.
	 *
	 * @param coordinate
	 * @throws ValidatorException
	 */
	protected void validateCoordinate(Element coordinate) throws ValidatorException
	{
		if (coordinate == null)
		{
			throw new ValidatorException("Coordinate is not defined");
		}

		if (!coordinate.hasAttribute("x") || !coordinate.hasAttribute("y"))
		{
			throw new ValidatorException("Coordinate is invalid.");
		}

		int x = Integer.parseInt(coordinate.getAttribute("x"));
		int y = Integer.parseInt(coordinate.getAttribute("y"));

		if (x < 0 || x > PlayBoard.SIZE || y < 0 || y > PlayBoard.SIZE)
		{
			throw new ValidatorException("Coordinate is out of bounds.");
		}
	}


	/**
	 * Validuje seznam historie.
	 *
	 * @param list
	 * @throws ValidatorException
	 */
	protected void validateHistoryList(NodeList list) throws ValidatorException
	{
		for (int i = 0; i < list.getLength(); i++)
		{
			Element itemElement = (Element) list.item(i);
			Element fromElement = (Element) itemElement.getElementsByTagName("from").item(0);
			Element toElement = (Element) itemElement.getElementsByTagName("to").item(0);
			Element playerOnTheMoveElement = (Element) itemElement.getElementsByTagName("playerOnTheMove").item(0);
			Element boardElement = (Element) itemElement.getElementsByTagName("board").item(0);

			validateCoordinate(fromElement);
			validateCoordinate(toElement);
			validatePlayerOnMove(playerOnTheMoveElement);
			validateBoard(boardElement);
		}
	}


	/**
	 * Validuje historii.
	 *
	 * @param history
	 * @throws ValidatorException
	 */
	public void validateHistory(Element history) throws ValidatorException
	{
		if (history == null)
		{
			throw new ValidatorException("History is not defined");
		}

		Element undos = (Element) history.getElementsByTagName("undos").item(0);
		Element redos = (Element) history.getElementsByTagName("redos").item(0);

		if (undos != null)
		{
			validateHistoryList(undos.getElementsByTagName("undo"));
		}

		if (redos != null)
		{
			validateHistoryList(redos.getElementsByTagName("redo"));
		}
	}
}
