package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class HistoryException extends Exception
{
	public HistoryException()
	{
		super();
	}


	public HistoryException(String msg)
	{
		super(msg);
	}


	public HistoryException(String msg, Throwable e)
	{
		super(msg, e);
	}
}