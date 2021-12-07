package MazeExceptions;

/**
 * Exception that is supposed to be thrown, when the Maze object has no MazeGenerator assigned to it.
 */
public class NoMazeGeneratorFoundException extends Exception{
    public NoMazeGeneratorFoundException(){super();}
    public NoMazeGeneratorFoundException(String message){super(message);}
    public NoMazeGeneratorFoundException(String message, Throwable cause){super(message, cause);}
    public NoMazeGeneratorFoundException(Throwable cause){super(cause);}
}
