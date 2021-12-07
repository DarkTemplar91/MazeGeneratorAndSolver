package MazeExceptions;

/**
 * Exception that is supposed to be thrown, when the Maze object has no MazeSolver assigned to it.
 */
public class NoMazeSolverFoundException extends Exception{
    public NoMazeSolverFoundException(){super();}
    public NoMazeSolverFoundException(String message){super(message);}
    public NoMazeSolverFoundException(String message, Throwable cause){super(message, cause);}
    public NoMazeSolverFoundException(Throwable cause){super(cause);}
}
