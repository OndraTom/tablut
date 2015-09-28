package tablut;

import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Koordináta - obdelníček na kraji hrací desky.
 *
 * @author Ondřej Tom
 */
public class TablutCoordinate extends JPanel
{
	/**
	 * Vertikální směr.
	 */
	public static String WAY_VERTICAL = "vertical";


	/**
	 * @param value
	 * @param way
	 */
	public TablutCoordinate(int value, String way)
	{
		super();

		setLayout(new GridBagLayout());

		add(getLabel(value, way));
	}


	/**
	 * Vrátí label koordinaty.
	 *
	 * @param value
	 * @param way
	 * @return
	 */
	private JLabel getLabel(int value, String way)
	{
		JLabel label = new JLabel(getCoordinateText(value, way));

		label.setFont(new Font("Verdana", 2, 20));

		return label;
	}


	/**
	 * Vrátí text pro label koordinaty.
	 *
	 * @param value
	 * @param way
	 * @return
	 */
	public static String getCoordinateText(int value, String way)
	{
		String text;

		if (way.equals(WAY_VERTICAL))
		{
			text = Integer.toString(PlayBoard.SIZE + 1 - value);
		}
		else
		{
			text = String.valueOf((char) (65 + value));
		}

		return text;
	}
}
