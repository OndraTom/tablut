package tablut.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Status Bar.
 *
 * @author Ondřej Tom
 */
public class GUIStatusBar extends JPanel
{
	/**
	 * Maximální počet zahraných tahů, bez zajmutí.
	 */
	private int blindMovesCountLimit;


	/**
	 * Počet zahraných tahů, bez zajmutí.
	 */
	private int blindMovesCount = 0;


	/**
	 * Počet ruských zajatců.
	 */
	private int russiansCaptivesCount = 0;


	/**
	 * Počet švédských zajatců.
	 */
	private int swedesCaptivesCount = 0;


	/**
	 * Informace pro hráče.
	 */
	private String infoText = "";


	/**
	 * Label pro maximální počet zahraných tahů, bez zajmutí.
	 */
	private JLabel blindMovesCountLabel;


	/**
	 * Label s počty zajatců.
	 */
	private JLabel captivesCountsLabel;


	/**
	 * Label pro informace pro hráče.
	 */
	private JLabel infoTextLabel;


	/**
	 * @param blindMovesCountLimit
	 */
	public GUIStatusBar(int blindMovesCountLimit)
	{
		super(new GridLayout(1, 0));
		setPreferredSize(new Dimension(100, 20));

		this.blindMovesCountLimit	= blindMovesCountLimit;
		this.blindMovesCountLabel	= new JLabel();
		this.captivesCountsLabel	= new JLabel();
		this.infoTextLabel			= new JLabel();

		setLabels();

		add(this.blindMovesCountLabel);
		add(this.captivesCountsLabel);
		add(this.infoTextLabel);

		// Tohle je ojeb, na kterém se mi zatím nechce pálit čas.
		// Chci napozicovat prvky vlevo dolů.
		add(new JPanel());
		add(new JPanel());
		add(new JPanel());
	}


	/**
	 * Nastaví labely.
	 */
	private void setLabels()
	{
		setBlindMovesCountLabel();
		setCaptivesCountLabel();
		setInfoTextLabel();
	}


	/**
	 * Nastaví label pro tahy.
	 */
	private void setBlindMovesCountLabel()
	{
		blindMovesCountLabel.setText("Blind moves: " + blindMovesCount + " / " + blindMovesCountLimit);
	}


	/**
	 * Nastaví label s počty zajatců.
	 */
	private void setCaptivesCountLabel()
	{
		captivesCountsLabel.setText("<html>Captives: " + russiansCaptivesCount + " / <font color='red'>" + swedesCaptivesCount + "</font></html>");
	}


	/**
	 * Nastaví labely pro info text.
	 */
	private void setInfoTextLabel()
	{
		infoTextLabel.setText(infoText);
	}


	/**
	 * Nastaví počet provedených tahů bez zajmutí.
	 *
	 * @param count
	 */
	public void setBlindMovesCount(int count)
	{
		blindMovesCount = count;
		setBlindMovesCountLabel();
	}


	/**
	 * Nastaví počty zajatců.
	 *
	 * @param russiansCaptivesCount
	 * @param swedesCaptivesCount
	 */
	public void setCaptivesCounts(int russiansCaptivesCount, int swedesCaptivesCount)
	{
		this.russiansCaptivesCount	= russiansCaptivesCount;
		this.swedesCaptivesCount	= swedesCaptivesCount;
		setCaptivesCountLabel();
	}


	/**
	 * Nastaví info text.
	 *
	 * @param text
	 */
	public void setInfoText(String text)
	{
		infoText = text;
		setInfoTextLabel();
	}


	/**
	 * Vyčistí info text.
	 */
	public void clearInfoText()
	{
		setInfoText("");
	}
}
