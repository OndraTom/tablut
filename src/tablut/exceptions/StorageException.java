package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class StorageException extends Exception
{
	public StorageException()
	{
		super();
	}


	public StorageException(String msg)
	{
		super(msg);
	}


	public StorageException(String msg, Throwable e)
	{
		super(msg, e);
	}
}