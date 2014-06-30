package semblance.results;

/**
 *
 * @author balnave
 */
public interface IResult {

    public String getName();
    
    public String getSource();
    
    public String getMessage();

    public String getReason();
    
    public int getLine();
    
    public int getParagraph();
     
    public long getExecutionTimeMs();
    
    public boolean hasPassed();

    public boolean hasFailed();
    
    public boolean hasError();

}
