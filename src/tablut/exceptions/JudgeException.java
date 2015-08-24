package tablut.exceptions;

/**
 * @author Ondřej Tom
 */
public class JudgeException extends Exception
{
    public JudgeException()
	{
        super();
    }


    public JudgeException(String msg)
	{
        super(msg);
    }


    public JudgeException(String msg, Throwable e)
	{
        super(msg, e);
    }
}