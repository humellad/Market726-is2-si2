package exceptions;
public class ParamNullException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public ParamNullException()
  {
    super();
  }
  /**This exception is triggered if the date of the product to sell is previous than today
  *@param s String of the exception
  */
  public ParamNullException(String s)
  {
    super(s);
  }
}