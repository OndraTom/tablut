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
import tablut.HistoryItem;
import tablut.HumanPlayer;
import tablut.Manager;
import tablut.PcPlayPauseButton;
import tablut.PlayBoard;
import tablut.RedoButton;
import tablut.Storage;
import tablut.TablutCoordinate;
import tablut.TablutSquare;
import tablut.UndoButton;

/**
 * GUI hry.
 *
 * @author Ondřej Tom
 */
public class GUIGame extends javax.swing.JFrame implements ChangeGUIListener, ChangePlayersSettingsListener
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
	List<LoadGameListener> listeners = new ArrayList<>();


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
		this.manager			= manager;
		this.board				= manager.getPlayBoard();
		this.history			= manager.getHistory();
		this.storage			= new Storage();
		this.playersSettings	= new GUIPlayersSettings(manager);

		// Nastaví GUI jako posluchače pro událost změny nastavení hráčů.
		playersSettings.addListener(this);

		// Spustí inicializaci.
		initGUI();
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


	/**
	 * Vrátí grafický objekt hrací desky.
	 */
	private JPanel createTablutBoard()
	{
		int i, j;

		// Dvourozměrné pole hrací desky - řádek, sloupec.
		int[][] board = this.board.getBoard();

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
	 * @param dataList
	 */
	private JScrollPane createHistoryList(List<HistoryItem> dataList)
	{
		// Vytvoří list z předaných dat.
		JList list = new JList(dataList.toArray());
		list.setEnabled(false);
		list.setPreferredSize(new Dimension(120, list.getPreferredSize().height));

		// Vytvoří scrollovací panel z listu.
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 280));

		return scrollPane;
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
		historyPanel.add(createHistoryList(history.getUndoItems()));
		historyPanel.add(redoButton);
		historyPanel.add(createHistoryList(history.getRedoItems()));

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
				try
				{
					int[][] bestMove = manager.getBestMove();
					JOptionPane.showMessageDialog(null, "Best move: " +
							TablutCoordinate.getCoordinateText(bestMove[0][0], "vertical") +
							TablutCoordinate.getCoordinateText(bestMove[0][1], "horizontal") +
							"  >  " +
							TablutCoordinate.getCoordinateText(bestMove[1][0], "vertical") +
							TablutCoordinate.getCoordinateText(bestMove[1][1], "horizontal"));
				}
				catch (PlayerException pe)
				{
					JOptionPane.showMessageDialog(null, pe.getMessage());
				}
			}
		});

		gameMenu.add(newGame);
		gameMenu.add(options);
		gameMenu.addSeparator();
		gameMenu.add(save);
		gameMenu.add(load);
		gameMenu.addSeparator();
		gameMenu.add(exit);

		helpMenu.add(bestMove);

		menuBar.add(gameMenu);
		menuBar.add(helpMenu);

		return menuBar;
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

		this.setJMenuBar(createMenu());

		// Zabalíme připravený frame.
		pack();

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(((screenDim.width - getSize().width) / 2), (screenDim.height - getSize().height) / 2);
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
		this.manager	= manager;
		board			= manager.getPlayBoard();
		history			= manager.getHistory();

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
			manager.setPlayerA(new ComputerPlayer(event.getPlayerADifficultyIndex() + 2));
		}
		// Player A - člověk.
		else
		{
			manager.setPlayerA(new HumanPlayer());
		}

		// Player B - počítač.
		if (event.getPlayerBTypeIndex() == 1)
		{
			manager.setPlayerB(new ComputerPlayer(event.getPlayerBDifficultyIndex() + 2));
		}
		// Player B - člověk.
		else
		{
			manager.setPlayerB(new HumanPlayer());
		}

		changeGUI(manager);
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
