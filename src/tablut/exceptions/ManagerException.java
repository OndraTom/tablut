package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class ManagerException extends Exception
{
	public ManagerException()
	{
		super();
	}


	public ManagerException(String msg)
	{
		super(msg);
	}


	public ManagerException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
