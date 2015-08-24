package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class PlayerException extends Exception
{
	public PlayerException()
	{
		super();
	}


	public PlayerException(String msg)
	{
		super(msg);
	}


	public PlayerException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
