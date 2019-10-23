package util.exception;

/**
 *
 * @author Zheng Yang
 */
public class CustomerEmailExistException extends Exception {

    /**
     * Creates a new instance of <code>CustomerEmailExistException</code>
     * without detail message.
     */
    public CustomerEmailExistException() {
    }

    /**
     * Constructs an instance of <code>CustomerEmailExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerEmailExistException(String msg) {
        super(msg);
    }
}
