package tablut.gui;

import tablut.listeners.LoadGameListener;
import tablut.listeners.ChangePlayersSettingsListener;
import tablut.listeners.ChangeGUIListener;
import tablut.events.LoadGameEvent;
import tablut.events.ChangePlayersSettingsEvent;
import tablut.events.ChangeGUIEvent;
import tablut.exceptions.StorageException;
import tablut.exceptions.PlayerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import tablut.ComputerPlayer;
import tablut.History;
import tablut.HumanPlayer;
import tablut.Judge;
import tablut.Manager;
import tablut.PcPlayPauseButton;
import tablut.PlayBoard;
import tablut.RedoButton;
import tablut.Storage;
import tablut.TablutCoordinate;
import tablut.TablutSquare;
import tablut.UndoButton;
import tablut.listeners.MarkSquareListener;
import tablut.listeners.PcIsThinkingListener;

/**
 * GUI hry.
 *
 * @author Ondřej Tom
 */
public class GUIGame extends javax.swing.JFrame implements ChangeGUIListener, ChangePlayersSettingsListener, PcIsThinkingListener, MarkSquareListener
{
	/**
	 * Manažer - řídí běh celé hry.
	 */
	private Manager manager;


	/**
	 * Logická reprezentace hrací desky.
	 */
	private PlayBoard board;


	/**
	 * Fyzická reprezentace hrací desky.
	 */
	private JButton[][] squares;


	/**
	 * Historie tahů.
	 */
	private History history;


	/**
	 * Kontejner obsahující všechny graf. prvky.
	 */
	private JPanel container;


	/**
	 * Úložiště - řeší načítání a ukládání hry.
	 */
	private Storage storage;


	/**
	 * Nastavení hráčů.
	 */
	private GUIPlayersSettings playersSettings;


	/**
	 * Pole posluchačů události LoadGameEvent.
	 */
	private List<LoadGameListener> listeners = new ArrayList<>();


	/**
	 * Informační text pro hráče.
	 */
	private GUIStatusBar statusBar;


	/**
	 * Frame s popisem pravidel hry.
	 */
	private GUIRules rulesFrame;


	/**
	 * Inicializace GUI.
	 *
	 * @param manager
	 */
	public GUIGame(Manager manager)
	{
		// Zrušíme možnost roztahovat okno.
		this.setResizable(false);

		// Nastavení objektů GUI.
		setManager(manager);
		this.board				= manager.getPlayBoard();
		this.history			= manager.getHistory();
		this.storage			= new Storage();
		this.playersSettings	= new GUIPlayersSettings(manager);
		this.statusBar			= new GUIStatusBar(Judge.BLIND_MOVES_MAX_COUNT);

		// Nastaví GUI jako posluchače pro událost změny nastavení hráčů.
		playersSettings.addListener(this);

		// Spustí inicializaci.
		initGUI();
		
		// Vycentrování okna.
		centerFrame();
	}
	
	
	/**
	 * Vycentruje okno.
	 */
	private void centerFrame()
	{
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(((screenDim.width - getSize().width) / 2), (screenDim.height - getSize().height) / 2);
	}


	/**
	 * Nastaví manažera.
	 *
	 * @param manager
	 */
	private void setManager(Manager manager)
	{
		this.manager = manager;
		this.manager.addPcIsThinkingListener(this);
	}


	/**
	 * Přidá posluchače pro událost načtení hry.
	 *
	 * @param listener
	 */
	public void addLoadGameListener(LoadGameListener listener)
	{
		listeners.add(listener);
	}


	/**
	 * Vyvolání události načtení hry.
	 *
	 * @param manager
	 */
	private void loadGame(Manager manager)
	{
		// Zavolá metodu loadGame() na všech posluchačích.
		for (LoadGameListener listener : listeners)
		{
			listener.loadGame(new LoadGameEvent(this, manager));
		}
	}


	/**
	 * Vytvoří novou hru (velice zjednodušeně a nešetrně vůči paměti).
	 */
	private void newGame()
	{
		// Vytvoří úvodní options.
		new GUIOptions().setVisible(true);

		// Skryje aktivní okno.
		this.setVisible(false);
	}


	@Override
	public void markSquare(int x, int y)
	{
		TablutSquare s = (TablutSquare) squares[x][y];
		s.markAsPlaying();
	}


	@Override
	public void unmarkSquare(int x, int y)
	{
		TablutSquare s = (TablutSquare) squares[x][y];
		s.unmark();
	}


	/**
	 * Vrátí grafický objekt hrací desky.
	 */
	private JPanel createTablutBoard()
	{
		int i, j;

		// Dvourozměrné pole hrací desky - řádek, sloupec.
		int[][] board = this.board.getBoard();

		// Tlačítka si po vytvoření uložíme.
		squares = new JButton[board.length][board.length];

		// Inicializace hrací desky.
		JPanel tablutBoard = new JPanel(new GridLayout(0, PlayBoard.SIZE + 2));
		tablutBoard.setBorder(new LineBorder(Color.BLACK));

		// Vložíme buttony na hrací desku.
		for (i = 0; i < board.length; i++)
		{
			// Vložíme vertikální koordinátu.
			tablutBoard.add(new TablutCoordinate(i, "vertical"));

			for (j = 0; j < board[i].length; j++)
			{
				// Vytvoříme button a nastavíme managera jako posluchače.
				JButton b = new TablutSquare(i, j, board[i][j], this.board.isProtectedField(new int[]{i, j}));
				b.addActionListener(manager);

				// Uložíme do pole.
				squares[i][j] = b;

				// Přidáme tlačítko na desku.
				tablutBoard.add(b);
			}
		}

		// Vložíme horizontální koordináty.
		tablutBoard.add(new JPanel());

		for (i = 0; i < board.length; i++)
		{
			tablutBoard.add(new TablutCoordinate(i, "horizontal"));
		}


		return tablutBoard;
	}


	/**
	 * Vrátí tlačítko pro pozastavení a znovuspuštění hry počítače.
	 *
	 * @param text
	 * @return
	 */
	private JPanel createPcPlayPauseButton()
	{
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(new PcPlayPauseButton(manager));

		return buttonPanel;
	}


	/**
	 * Vrátí scrollovací list historie (undo/redo).
	 *
	 * @param history
	 */
	private GUIHistoryScrollPane createHistoryList(History history)
	{
		GUIHistoryList historyList = new GUIHistoryList(history);

		historyList.addListener(manager);
		historyList.setCellRenderer(new GUIHistoryListCellRenderer(manager.getPlayerOnMove(), historyList.getSelectedIndex()));

		// Vytvoříme a vátíme scrollovací panel z listu.
		return new GUIHistoryScrollPane(historyList);
	}


	/**
	 * Vrátí panel historie.
	 */
	private JPanel createHistoryPanel()
	{
		// Vytvoří panel.
		JPanel historyPanel = new JPanel();
		historyPanel.setPreferredSize(new Dimension(150, 640));

		// Undo button.
		JButton undoButton = new UndoButton();
		undoButton.setPreferredSize(new Dimension(125, 25));
		undoButton.addActionListener(manager);
		if (history.getUndoItems().isEmpty())
		{
			undoButton.setEnabled(false);
		}

		// Redo button.
		JButton redoButton = new RedoButton();
		redoButton.setPreferredSize(new Dimension(125, 25));
		redoButton.addActionListener(manager);
		if (history.getRedoItems().isEmpty())
		{
			redoButton.setEnabled(false);
		}

		// Poskládáme prvky panelu.
		historyPanel.add(undoButton);
		historyPanel.add(redoButton);
		historyPanel.add(createHistoryList(history));

		return historyPanel;
	}


	/**
	 * Vrátí menu.
	 */
	private JMenuBar createMenu()
	{
		// Menu.
		JMenuBar menuBar	= new JMenuBar();

		// Položky menu.
		JMenu gameMenu		= new JMenu("Game");
		JMenuItem newGame	= new JMenuItem("New");
		JMenuItem options	= new JMenuItem("Options");
		JMenuItem save		= new JMenuItem("Save");
		JMenuItem load		= new JMenuItem("Load");
		JMenuItem exit		= new JMenuItem("Exit");

		JMenu helpMenu		= new JMenu("Help");
		JMenuItem rules		= new JMenuItem("Rules");
		JMenuItem bestMove	= new JMenuItem("Best move");

		// Nastavení vlastností položek menu.
		newGame.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					newGame();
				}
			}
		);

		options.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					playersSettings.setVisible(true);
				}
			}
		);

		save.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
					fileChooser.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JFileChooser s = (JFileChooser) e.getSource();
							File f = s.getSelectedFile();

							try
							{
								storage.save(manager, f);
							}
							catch (StorageException se)
							{
								System.out.println(se.getMessage());
							}
						}
					});
					fileChooser.showSaveDialog(null);
				}
			}
		);

		load.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
					fileChooser.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JFileChooser s = (JFileChooser) e.getSource();
							File f = s.getSelectedFile();

							try
							{
								loadGame(storage.load(f));
							}
							catch (StorageException se)
							{
								// todo: otestovat chybu, vyhodit dialog.
								JOptionPane.showMessageDialog(null, se.getMessage());
							}
						}
					});
					fileChooser.showOpenDialog(null);
				}
			}
		);

		exit.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			}
		);

		bestMove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Nápovědu nejlepšího tahu pustíme v novém vlákně.
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						try
						{
							if (manager.getWinner() == 0)
							{
								String infoText = statusBar.getInfoText();

								startThinking();
								int[][] bestMove = manager.getBestMove();
								stopThinking();

								statusBar.setInfoText(infoText);

								JOptionPane.showMessageDialog(null, "Best move: " +
										TablutCoordinate.getCoordinateText(bestMove[0][1], "horizontal") +
										TablutCoordinate.getCoordinateText(bestMove[0][0], "vertical") +
										"  >  " +
										TablutCoordinate.getCoordinateText(bestMove[1][1], "horizontal") +
										TablutCoordinate.getCoordinateText(bestMove[1][0], "vertical"));
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Game over - your best move is to start new one :)");
							}
						}
						catch (PlayerException pe)
						{
							JOptionPane.showMessageDialog(null, pe.getMessage());
						}

					}

				});

				thread.start();
			}
		});

		rules.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				showRules();
			}
		});

		gameMenu.add(newGame);
		gameMenu.add(options);
		gameMenu.addSeparator();
		gameMenu.add(save);
		gameMenu.add(load);
		gameMenu.addSeparator();
		gameMenu.add(exit);

		helpMenu.add(rules);
		helpMenu.add(bestMove);

		menuBar.add(gameMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}


	/**
	 * Nastaví oznámení o vítězi do status baru.
	 */
	private void setWinnerText()
	{
		if (manager.getWinner() != 0)
		{
			if (manager.getWinner() == TablutSquare.RUSSIAN)
			{
				statusBar.setInfoText("Russians win!");
			}
			else
			{
				statusBar.setInfoText("Swedes win!");
			}
		}
	}


	/**
	 * Inicializace GUI.
	 */
	private void initGUI()
	{
		// Inicializace kontejneru pro grafické objekty.
		container = new JPanel();

		// Při zavření framu vypne aplikaci.
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Přidáme prvky do framu.
		container.add(createTablutBoard());

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

		if (manager.isComputerPlayerInGame())
		{
			rightPanel.add(createPcPlayPauseButton());
		}

		rightPanel.add(createHistoryPanel());

		container.add(rightPanel);

		getContentPane().add(container);

		getContentPane().add(statusBar, BorderLayout.SOUTH);

		this.setJMenuBar(createMenu());

		// Zabalíme připravený frame.
		pack();
	}


	/**
	 * Akce - změna GUI.
	 *
	 * @param manager
	 */
	public void changeGUI(Manager manager)
	{
		// Smaže stávající kontejner.
		this.remove(container);

		// Nastavení objektů GUI.
		setManager(manager);
		board			= manager.getPlayBoard();
		history			= manager.getHistory();

		statusBar.setBlindMovesCount(manager.getBlindMovesCount());
		statusBar.setCaptivesCounts(manager.getRussiansCaptivesCount(), manager.getSwedesCaptivesCount());

		// Pokud máme výherce, tak jej zobrazíme ve status baru.
		setWinnerText();

		// Inicializuje nový kontejner (s aktuálními daty).
		this.initGUI();
	}


	/**
	 * Metoda volaná po vykonaném tahu.
	 *
	 * @param event obsahuje zdroj, hrací desku a historii.
	 */
	@Override
	public void changeGUI(ChangeGUIEvent event)
	{
		this.changeGUI((Manager) event.getSource());
	}


	/**
	 * Událost - změna nastavení hráčů.
	 *
	 * @param event
	 */
	@Override
	public void changePlayersSettings(ChangePlayersSettingsEvent event)
	{
		// Player A - počítač.
		if (event.getPlayerATypeIndex() == 1)
		{
			manager.setPlayerA(new ComputerPlayer(event.getPlayerADifficultyIndex() == 1 ? 0 : event.getPlayerADifficultyIndex() + 1));
		}
		// Player A - člověk.
		else
		{
			manager.setPlayerA(new HumanPlayer());
		}

		// Player B - počítač.
		if (event.getPlayerBTypeIndex() == 1)
		{
			manager.setPlayerB(new ComputerPlayer(event.getPlayerBDifficultyIndex() == 1 ? 0 : event.getPlayerBDifficultyIndex() + 1));
		}
		// Player B - člověk.
		else
		{
			manager.setPlayerB(new HumanPlayer());
		}

		changeGUI(manager);
	}


	/**
	 * Voláno při zahájení generování.
	 */
	@Override
	public void startThinking()
	{
		setEnabled(false);
		statusBar.setInfoText("PC is thinking...");
	}


	/**
	 * Voláno při dokončení generování.
	 */
	@Override
	public void stopThinking()
	{
		statusBar.clearInfoText();
		setEnabled(true);
	}


	/**
	 * Zobrazí pravidla hry.
	 */
	private void showRules()
	{
		if (rulesFrame == null)
		{
			rulesFrame = new GUIRules();
		}

		rulesFrame.setVisible(true);
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
