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
import tablut.listeners.HistoryListListener;
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
public class Manager implements ActionListener, HistoryListListener
{
	/**
	 * Minimální zdržení PC hráče (milisekundy).
	 */
	private static int PC_PLAYER_DELAY = 500;


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
	private boolean gamePaused = false;


	/**
	 * Hloubka ignorace tahů.
	 */
	private int ignoredMovesDepth = -1;


	/**
	 * Příznak pro úplné zastavení manažera.
	 */
	private boolean isStopped = false;


	/**
	 * @param playerA		index hráče č. 1
	 * @param playerB		index hráče č. 2
	 * @param difficultyA	obtížnost hráče č. 1
	 * @param difficultyB	obtížnost hráče č. 2
	 */
	public Manager(int playerA, int playerB, int difficultyA, int difficultyB)
	{
		difficultyA = difficultyA == 1 ? 0 : difficultyA + 1;
		difficultyB = difficultyB == 1 ? 0 : difficultyB + 1;

		this.judge		= new Judge(board);
		this.playerA	= PlayerFactory.createPlayer(playerA, difficultyA);
		this.playerB	= PlayerFactory.createPlayer(playerB, difficultyB);
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
	 * Zastavení manažera - přerušení hlavní smyčky.
	 */
	public void stop()
	{
		this.isStopped = true;
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
	 * Vrací oponenta.
	 *
	 * @return
	 */
	private Player getOponent()
	{
		if (playerOnMove == TablutSquare.RUSSIAN)
		{
			return playerB;
		}
		else
		{
			return playerA;
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
	 * Kromě samotného označení pole s kamenem pro táhnutí označíme také
	 * pole na které hráč může táhnout (nápověda).
	 *
	 * @param x
	 * @param y
	 */
	private void markSquare(int x, int y)
	{
		for (MarkSquareListener l : markSquareListeners)
		{
			l.markSquare(x, y);

			// Označíme nápovědné tahy.
			for (int[] move : judge.getPossibleMoves(new int[]{x, y}))
			{
				l.markSquareAsHint(move[0], move[1]);
			}
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
		if (isPlayerOnMoveHuman() && moveFrom != null && moveTo != null)
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
	public boolean isPlayerOnMoveHuman()
	{
		return (this.getPlayer() instanceof HumanPlayer);
	}


	/**
	 * Zkontroluje, zda-li je hráčem na tahu počítač.
	 *
	 * @return
	 */
	public boolean isPlayerOnMoveComputer()
	{
		return (this.getPlayer() instanceof ComputerPlayer);
	}


	/**
	 * Zjistí, zda-li je ve hře lidský hráč.
	 *
	 * @return
	 */
	protected boolean isHumanPlayerInGame()
	{
		return (getPlayerA() instanceof HumanPlayer) || (getPlayerB() instanceof HumanPlayer);
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
				// Hráč klikl na svůj kámen - nastavíme ho tedy jako nový tah "z".
				if (this.isPlayerOnMoveSquare(x, y))
				{
					unmarkSquare(moveFrom[0], moveFrom[1]);
					moveFrom = new int[]{x, y};
					markSquare(x, y);
				}

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
	@Override
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
		// Kliknutí na hrací pole.
		if (isPlayerOnMoveHuman() && evt.getSource() instanceof TablutSquare && !this.isGameOver())
		{
			TablutSquare square = (TablutSquare) evt.getSource();
			this.addMove(square.getXCoord(), square.getYCoord());
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
		return ComputerPlayer.getBestMove(judge, playerOnMove, 4, null, 0);
	}


	/**
	 * Pozastaví hru.
	 */
	private void pauseGame()
	{
		gamePaused = true;
	}


	/**
	 * Obnoví hru.
	 */
	private void resumeGame()
	{
		gamePaused = false;
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
	public boolean isGameOver()
	{
		return winner > 0 || this.judge.isBlindMovesCountReached();
	}


	/**
	 * Vrátí počet ruských zajatců.
	 *
	 * @return
	 */
	public int getRussiansCaptivesCount()
	{
		return judge.getPlayersCaptivesCount(TablutSquare.RUSSIAN);
	}


	/**
	 * Vrátí počet švédských zajatců.
	 *
	 * @return
	 */
	public int getSwedesCaptivesCount()
	{
		return judge.getPlayersCaptivesCount(TablutSquare.SWEDEN);
	}


	/**
	 * Zjístí, zda-li hra osciluje.
	 *
	 * @return
	 */
	private boolean isGameOscilating()
	{
		if (!isHumanPlayerInGame())
		{
			ComputerPlayer player	= (ComputerPlayer) getPlayer();
			ComputerPlayer oponent	= (ComputerPlayer) getOponent();

			return player.getDifficulty() <= oponent.getDifficulty() && judge.areMovesInCycle(history);
		}

		return false;
	}


	/**
	 * Odstartuje hrací smyčku.
	 *
	 * @throws InterruptedException
	 * @throws ManagerException
	 */
	public void startGameLoop() throws InterruptedException, ManagerException
	{
		// Smyčka běží, dokud není definován vítěz, nebylo dosaženo maximálního počtu tahů nebo nedošlo k přerušení.
		while (!isGameOver() && !isStopped)
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
				// Zaznamenáme čas začátku zpracování PC hráče.
				long pcPlayerStartTime = System.currentTimeMillis();

				// Pokud je hra pozastavena, tak aplikace čeká.
				if (isGamePaused())
				{
					// Spinkej.
					ComputerPlayer.stopThinking();

					Thread.sleep(50);
					continue;
				}
				else
				{
					// Budíček.
					ComputerPlayer.startThinking();
				}

				try
				{
					int[][] ignoredMove = null;

					// Pokud pohyby oscilují, získáme ignorovaný tah.
					if (isGameOscilating())
					{
						ignoredMovesDepth++;

						HistoryItem ignoredItem = history.getUndoItems().get(
								history.getUndoItems().size() - Judge.MOVES_OSCILATING_LIMIT
						);

						ignoredMove = new int[][]{ignoredItem.getMoveFrom(), ignoredItem.getMoveTo()};
					}

					// Vygeneruje nejlepší tah, podle danné obtížnosti.
					int[][] computerMove = ComputerPlayer.getBestMove(judge, playerOnMove, getPlayerDifficulty(), ignoredMove, ignoredMovesDepth);

					// Nastaví tah.
					moveFrom = computerMove[0];
					moveTo = computerMove[1];

					// Vypne "PC thinking" mód.
					ComputerPlayer.stopThinking();
				}

				// Při zachycení výjimky vypíše zprávu.
				catch (PlayerException ex)
				{
					JOptionPane.showMessageDialog(null, ex.getMessage());
					break;
				}

				// Zamezíme hře provést příliš rychlý tah.
				while (System.currentTimeMillis() - pcPlayerStartTime < PC_PLAYER_DELAY)
				{
					Thread.sleep(50);
				}
			}

			try
			{
				// Pokud je hra pozastavena, přeskočíme na začátek cyklu.
				if (isPlayerOnMoveComputer() && isGamePaused())
				{
					moveFrom	= null;
					moveTo		= null;
					continue;
				}

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
						// Pokud hraje člověk proti počítači, tak po vykonání tahu
						// obnovíme hru (aby PC nebyl zbytečně pausnutý).
						if (this.isPlayerOnMoveHuman() && this.isComputerPlayerInGame())
						{
							this.resumeGame();
						}

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
