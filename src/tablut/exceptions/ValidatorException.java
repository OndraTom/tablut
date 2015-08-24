package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class ValidatorException extends Exception
{
	public ValidatorException()
	{
		super();
	}


	public ValidatorException(String msg)
	{
		super(msg);
	}


	public ValidatorException(String msg, Throwable e)
	{
		super(msg, e);
	}
}