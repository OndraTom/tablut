package tablut;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Políčko hrací desky (v GUI).
 *
 * @author Ondřej Tom
 */
public class TablutSquare extends JButton
{
	/**
	 * Hodnota pole ruského hráče.
	 */
	public static int RUSSIAN = 1;


	/**
	 * Hodnota pole švédského hráče.
	 */
	public static int SWEDEN = 2;


	/**
	 * Hodnota pole krále.
	 */
	public static int KING = 3;


	/**
	 * Koordináty, hodnota.
	 */
	private int x, y, value;


	/**
	 * Indikátor králova pole (a tím nemyslím část Brna :-D).
	 */
	private boolean isProtected;


	/**
	 * Odsazení.
	 */
	private Insets buttonMargin = new Insets(0, 0, 0, 0);


	/**
	 * Ikona pole.
	 */
	private ImageIcon baseIcon = new ImageIcon(
			new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));


	/**
	 * @param x
	 * @param y
	 * @param value
	 * @param isProtected
	 */
	public TablutSquare(int x, int y, int value, boolean isProtected)
	{
		this.x = x;
		this.y = y;
		this.value = value;
		this.isProtected = isProtected;

		setMargin(buttonMargin);
		setIcon(baseIcon);

		setDefaultBackground();
	}


	/**
	 * Nastaví výchozí pozadí pole.
	 */
	private void setDefaultBackground()
	{
		if (isProtected)
		{
			setBackground(Color.YELLOW);
		}
		else
		{
			setBackground(Color.WHITE);
		}
	}


	/**
	 * Vrací koordinát x.
	 *
	 * @return
	 */
	public int getXCoord()
	{
		return x;
	}


	/**
	 * Vrací koordinát y.
	 *
	 * @return
	 */
	public int getYCoord()
	{
		return y;
	}


	/**
	 * Vrací hodnotu pole.
	 *
	 * @return
	 */
	public int getCoordValue()
	{
		return value;
	}


	/**
	 * Vrací indikátor kr. pole.
	 *
	 * @return
	 */
	public boolean isProtected()
	{
		return isProtected;
	}


	/**
	 * Označí pole jako táhnoucí.
	 */
	public void markAsPlaying()
	{
		setBackground(Color.GRAY);
	}


	/**
	 * Odoznačí pole.
	 */
	public void unmark()
	{
		setDefaultBackground();
	}


	/**
	 * Vykreslí políčko.
	 *
	 * @param g
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		// Pokud se nejedná o prázdné pole, nakreslí kámen.
		if (value > 0)
		{
			Graphics2D g2d = (Graphics2D) g;

			switch (value)
			{
				case 1:
					g2d.setColor(Color.BLACK);
					break;
				case 2:
					g2d.setColor(Color.RED);
					break;
				case 3:
					g2d.setColor(Color.ORANGE);
					break;
			}

			g2d.fill(new Ellipse2D.Double(10, 10, 50, 50));
		}
	}
}
