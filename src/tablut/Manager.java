package tablut;

import tablut.listeners.ChangeGUIListener;
import tablut.events.ChangeGUIEvent;
import tablut.exceptions.PlayerException;
import tablut.exceptions.ManagerException;
import tablut.exceptions.JudgeException;
import tablut.exceptions.HistoryException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import tablut.listeners.MarkSquareListener;
import tablut.listeners.PcIsThinkingListener;

/**
 * Manažer.
 *
 * Dispečer veškeré herní logiky.
 * Obsahuje instance všech částí (ne GUI) a řídí jejich komunikaci.
 *
 * @author Ondřej Tom
 */
public class Manager implements ActionListener
{
	/**
	 * Hrací deska.
	 */
	private PlayBoard board = new PlayBoard();


	/**
	 * Rozhodčí.
	 */
	private Judge judge;


	/**
	 * Hráč č. 1 (Rus).
	 */
	private Player playerA;


	/**
	 * Hráč č. 2 (Švéd).
	 */
	private Player playerB;


	/**
	 * Hráč na tahu.
	 */
	private int playerOnMove = TablutSquare.RUSSIAN;


	/**
	 * Vítěz.
	 */
	private int winner = 0;


	/**
	 * Souřadnice pole, z kterého se táhne.
	 */
	private int[] moveFrom;


	/**
	 * Souřadnice pole, na které se táhne.
	 */
	private int[] moveTo;


	/**
	 * Historie tahů (undo/redo).
	 */
	private History history;


	/**
	 * Pole posluchačů události changeGUI.
	 */
	List<ChangeGUIListener> changeGuiListeners = new ArrayList<>();


	/**
	 * Pole posluchačů událostí spojených s označováním pole.
	 */
	List<MarkSquareListener> markSquareListeners = new ArrayList<>();


	/**
	 * Příznak pozastavené hry.
	 */
	boolean gamePaused = false;


	/**
	 * @param playerA		index hráče č. 1
	 * @param playerB		index hráče č. 2
	 * @param difficultyA	obtížnost hráče č. 1
	 * @param difficultyB	obtížnost hráče č. 2
	 */
	public Manager(int playerA, int playerB, int difficultyA, int difficultyB)
	{
		this.judge		= new Judge(board);
		this.playerA	= PlayerFactory.createPlayer(playerA, difficultyA + 2);
		this.playerB	= PlayerFactory.createPlayer(playerB, difficultyB + 2);
		this.history	= new History();
	}


	/**
	 * @param playerA
	 * @param playerB
	 * @param playerOnMove
	 * @param winner
	 * @param board
	 * @param history
	 */
	public Manager(Player playerA, Player playerB, int playerOnMove, int winner, PlayBoard board, History history)
	{
		this.playerA		= playerA;
		this.playerB		= playerB;
		this.playerOnMove	= playerOnMove;
		this.winner			= winner;
		this.board			= board;
		this.judge			= new Judge(board);
		this.history		= history;
	}


	/**
	 * Přidá posluchače generování nejlepšího tahu.
	 *
	 * @param listener
	 */
	public void addPcIsThinkingListener(PcIsThinkingListener listener)
	{
		ComputerPlayer.addPcIsThinkingListener(listener);
	}


	/**
	 * Vrátí počet zahraných tahů, bez zajmutí.
	 *
	 * @return
	 */
	public int getBlindMovesCount()
	{
		return judge.getBlindMovesCount();
	}


	/**
	 * Vrátí příznak toho, zda-li je ve hře počítačový hráč.
	 *
	 * @return
	 */
	public boolean isComputerPlayerInGame()
	{
		return playerA instanceof ComputerPlayer || playerB instanceof ComputerPlayer;
	}


	/**
	 * Vrací historii tahů.
	 *
	 * @return
	 */
	public History getHistory()
	{
		return history;
	}


	/**
	 * Vrací hrací desku.
	 *
	 * @return
	 */
	public PlayBoard getPlayBoard()
	{
		return board;
	}


	/**
	 * Vrátí hráče A.
	 *
	 * @return
	 */
	public Player getPlayerA()
	{
		return playerA;
	}


	/**
	 * Nastaví hráče A.
	 *
	 * @param player
	 */
	public void setPlayerA(Player player)
	{
		playerA = player;
	}


	/**
	 * Vrátí hráče B.
	 *
	 * @return
	 */
	public Player getPlayerB()
	{
		return playerB;
	}


	/**
	 * Nastaví hráče B.
	 *
	 * @param player
	 */
	public void setPlayerB(Player player)
	{
		playerB = player;
	}


	/**
	 * Vrátí index hráče na tahu.
	 *
	 * @return
	 */
	public int getPlayerOnMove()
	{
		return playerOnMove;
	}


	/**
	 * Vrátí index výherce.
	 *
	 * @return
	 */
	public int getWinner()
	{
		return winner;
	}


	/**
	 * Vrací hráče, který je na tahu.
	 *
	 * @return
	 */
	private Player getPlayer()
	{
		if (playerOnMove == TablutSquare.RUSSIAN)
		{
			return playerA;
		}
		else
		{
			return playerB;
		}
	}


	/**
	 * Vrací obtížnost hráče na tahu.
	 *
	 * @return
	 * @throws ManagerException pokud se nejedná o lidského hráče.
	 */
	private int getPlayerDifficulty() throws ManagerException
	{
		Player player = this.getPlayer();

		if (player instanceof ComputerPlayer)
		{
			ComputerPlayer computer = (ComputerPlayer) player;
			return computer.getDifficulty();
		}
		else
		{
			throw new ManagerException("Cant't take difficulty of human player.");
		}
	}


	/**
	 * Přepne hráče na tahu.
	 */
	private void changePlayerOnMove()
	{
		playerOnMove = judge.getOtherPlayer(playerOnMove);
	}


	/**
	 * Přidá posluchače události changeGUI.
	 *
	 * @param l
	 */
	public void addChangeGUIListener(ChangeGUIListener l)
	{
		changeGuiListeners.add(l);
	}


	/**
	 * Přidá posluchače událostí spojených s označováním pole.
	 *
	 * @param l
	 */
	public void addMarkSquareListener(MarkSquareListener l)
	{
		markSquareListeners.add(l);
	}


	/**
	 * Aktualizuje GUI.
	 *
	 * Na všech posluchačích zavolá metodu changeGUI.
	 */
	private void changeGUI()
	{
		for (ChangeGUIListener l : changeGuiListeners)
		{
			l.changeGUI(new ChangeGUIEvent(this));
		}
	}


	/**
	 * Obvolá posluchače označení pole.
	 *
	 * @param x
	 * @param y
	 */
	private void markSquare(int x, int y)
	{
		for (MarkSquareListener l : markSquareListeners)
		{
			l.markSquare(x, y);
		}
	}


	/**
	 * Obvolá posluchače odoznačení pole.
	 *
	 * @param x
	 * @param y
	 */
	private void unmarkSquare(int x, int y)
	{
		for (MarkSquareListener l : markSquareListeners)
		{
			l.unmarkSquare(x, y);
		}
	}


	/**
	 * Vyčistí tahy.
	 */
	private void clearMoves()
	{
		if (moveFrom != null && moveTo != null)
		{
			unmarkSquare(moveFrom[0], moveFrom[1]);
			unmarkSquare(moveTo[0], moveTo[1]);
		}

		moveFrom	= null;
		moveTo		= null;
	}


	/**
	 * Zkontroluje, zda-li je hráčem na tahu člověk.
	 *
	 * @return
	 */
	private boolean isPlayerOnMoveHuman()
	{
		return (this.getPlayer() instanceof HumanPlayer);
	}


	/**
	 * Zkontroluje, zda-li patří pole hráči na tahu.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isPlayerOnMoveSquare(int x, int y)
	{
		int value = board.getCoordsValue(x, y);

		// Král -> švédové.
		if (value == TablutSquare.KING)
		{
			return playerOnMove == TablutSquare.SWEDEN;
		}

		return playerOnMove == value;
	}



	/**
	 * Vloží tah.
	 *
	 * @param x
	 * @param y
	 */
	private void addMove(int x, int y)
	{
		// Pokud nebyl zadán tah "z", nastaví ho.
		if (moveFrom == null)
		{
			// Hráč musí táhnout s kamenem, který mu patří.
			if (!this.isPlayerOnMoveSquare(x, y))
			{
				return;
			}

			moveFrom = new int[]{x, y};
			markSquare(x, y);
		}

		// Pokud byl nastaven tah "z" a nebyl zadán tah "do", nastaví ho.
		else if (moveTo == null)
		{
			// Zrušení tahu "z" (hráč chce zřejmě vybrat jiné).
			if (x == moveFrom[0] && y == moveFrom[1])
			{
				moveFrom = null;
				unmarkSquare(x, y);
				return;
			}

			// Hráč musí táhnout na prázdné pole.
			if (!board.isCoordBlank(new int[]{x, y}))
			{
				return;
			}

			moveTo = new int[]{x, y};
		}
	}


	/**
	 * Zkontroluje, zda-li byl nastaven tah.
	 *
	 * @return
	 */
	private boolean isMoveSet()
	{
		return (moveFrom != null && moveTo != null);
	}


	/**
	 * Zahraje tah.
	 */
	private void playMove()
	{
		history.addUndo(new HistoryItem(playerOnMove, (PlayBoard) board.clone(), moveFrom, moveTo, getBlindMovesCount()));
		judge.playMove(moveFrom, moveTo, playerOnMove);
		this.clearMoves();
	}


	/**
	 * Provede skok v historii.
	 *
	 * @param hItem
	 * @param type
	 * @throws HistoryException
	 */
	private void doHistoryAction(HistoryItem hItem, String type) throws HistoryException
	{
		// Získá hráče na tahu.
		playerOnMove = hItem.getPlayerOnMove();

		// Nastavíme hrací desku.
		board = (PlayBoard) hItem.getBoard().clone();
		judge = new Judge(board);

		// Nastavíme počet zahraných tahů, bez zajmutí.
		judge.setBlindMovesCount(hItem.getBlindMovesCount());

		if (type.equals("redo"))
		{
			// Zahrajeme tah.
			judge.playMove(hItem.getMoveFrom(), hItem.getMoveTo(), hItem.getPlayerOnMove());

			// Změníme hráče na tahu.
			this.changePlayerOnMove();
		}

		// Nastavíme GUI.
		this.clearMoves();
		this.changeGUI();
	}


	/**
	 * Provede operaci undo.
	 *
	 * @throws HistoryException
	 */
	private void undo() throws HistoryException
	{
		// Získá tah undo z historie.
		HistoryItem hItem = history.getUndo();

		doHistoryAction(hItem, "undo");
	}


	/**
	 * Provede operaci redo.
	 *
	 * @throws HistoryException
	 */
	private void redo() throws HistoryException
	{
		// Získá tah redo z historie.
		HistoryItem hItem = history.getRedo();

		doHistoryAction(hItem, "redo");
	}


	/**
	 * Skočí na n-tou položku v historii.
	 *
	 * @param index
	 * @throws HistoryException
	 */
	public void goToHistoryItem(int index) throws HistoryException
	{
		// Pokus o přejetí na aktuální položku.
		if (history.getRedoItems().size() - index == 0)
		{
			// Ojeb kvůli tomu, že list po jednom kliku nemění selektovaný index.
			this.changeGUI();
			return;
		}

		pauseGame();

		String type = "undo";

		if (index < history.getRedoItems().size())
		{
			type = "redo";
		}

		HistoryItem hItem = history.getNthItem(index + 1);

		doHistoryAction(hItem, type);
	}


	/**
	 * Byla vykonána akce - kliknutí na tlačítko.
	 *
	 * @param evt
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		// Akci provedeme pouze, pokud je na tahu člověk.
		if (this.isPlayerOnMoveHuman())
		{
			// Kliknutí na hrací pole.
			if (evt.getSource() instanceof TablutSquare)
			{
				TablutSquare square = (TablutSquare) evt.getSource();
				this.addMove(square.getXCoord(), square.getYCoord());
			}
		}

		// Kliknutí na undo button.
		if (evt.getSource() instanceof UndoButton)
		{
			try
			{
				pauseGame();
				undo();
			}
			catch (HistoryException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}

		// Kliknutí na redo button.
		if (evt.getSource() instanceof RedoButton)
		{
			try
			{
				pauseGame();
				redo();
			}
			catch (HistoryException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}

		// Kliknutí na pozastavení/spuštění PC hry
		if (evt.getSource() instanceof PcPlayPauseButton)
		{
			changePausePlay();
			this.changeGUI();
		}
	}


	/**
	 * Oznámí vítěze.
	 */
	private void announceWinner()
	{
		if (winner == TablutSquare.RUSSIAN)
		{
			JOptionPane.showMessageDialog(null, "Russians win!");
		}
		else if (winner == TablutSquare.SWEDEN)
		{
			JOptionPane.showMessageDialog(null, "Swedes win!");
		}
	}


	/**
	 * Získá nejlepší tah.
	 *
	 * @return
	 * @throws PlayerException
	 */
	public int[][] getBestMove() throws PlayerException
	{
		return ComputerPlayer.getBestMove(judge, playerOnMove, 4);
	}


	/**
	 * Pozastaví hru.
	 */
	private void pauseGame()
	{
		gamePaused = true;
	}


	/**
	 * Zjistí, jestli je hra pozastavená.
	 *
	 * @return
	 */
	public boolean isGamePaused()
	{
		return gamePaused;
	}


	/**
	 * Pozastaví/spustí hru.
	 */
	private void changePausePlay()
	{
		gamePaused = !gamePaused;
	}


	/**
	 * Zjistí, jestli již nastal konec hry.
	 *
	 * @return
	 */
	private boolean isGameOver()
	{
		return winner > 0 || this.judge.isBlindMovesCountReached();
	}


	/**
	 * Odstartuje hrací smyčku.
	 *
	 * @throws InterruptedException
	 * @throws ManagerException
	 */
	public void startGameLoop() throws InterruptedException, ManagerException
	{
		// Smyčka běží, dokud není definován vítěz nebo nebylo dosaženo maximálního počtu tahů.
		while (!isGameOver())
		{
			// Pokud je hráčem na tahu člověk a nebyl nastaven tah, tak aplikace čeká.
			if (this.isPlayerOnMoveHuman() && !this.isMoveSet())
			{
				Thread.sleep(50);
				continue;
			}

			// Pokud je hráčem na tahu počítač a nebyl nastaven tah.
			else if (!this.isPlayerOnMoveHuman() && !this.isMoveSet())
			{
				// Pokud je hra pozastavena, tak aplikace čeká.
				if (isGamePaused())
				{
					Thread.sleep(50);
					continue;
				}

				try
				{
					// Vygeneruje nejlepší tah, podle danné obtížnosti.
					int[][] computerMove = ComputerPlayer.getBestMove(judge, playerOnMove, getPlayerDifficulty());

					// Nastaví tah.
					moveFrom = computerMove[0];
					moveTo = computerMove[1];
				}

				// Při zachycení výjimky vypíše zprávu.
				catch (PlayerException ex)
				{
					JOptionPane.showMessageDialog(null, ex.getMessage());
					break;
				}
			}

			try
			{
				// Zkontrolujeme, zda-li je tah validní.
				if (judge.isMoveValid(moveFrom, moveTo))
				{

					// Zrahrajeme tah.
					this.playMove();

					// Pokud byl král zajat, nastavíme vítěze a vypíšeme zprávu.
					if (judge.isKingCaptured())
					{
						winner = TablutSquare.RUSSIAN;
					}

					// Pokud byl král zachráněn, nastavíme vítěze a vypíšeme zprávu.
					else if (judge.isKingSave())
					{
						winner = TablutSquare.SWEDEN;
					}
					else
					{
						// Vyměníme hráče na tahu.
						this.changePlayerOnMove();
					}

					// Po zahrání tahu, je třeba aktualizovat GUI.
					this.changeGUI();

				}

				// Pokud tah není validní, smažeme tahy a vypíšeme zprávu.
				else
				{
					this.clearMoves();
					JOptionPane.showMessageDialog(null, "Move is not valid.");
				}
			}

			// Při zachycení výjimky vypíše zprávu.
			catch (JudgeException ex)
			{
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}

		// Pokud byl dosažen max. počet tahů -> vypíšeme zprávu.
		if (this.judge.isBlindMovesCountReached())
		{
			JOptionPane.showMessageDialog(null, "You have reached maximum count of possible moves without taking a stone.");
		}

		if (winner > 0)
		{
			announceWinner();
		}
	}
}
