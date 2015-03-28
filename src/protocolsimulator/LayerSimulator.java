package protocolsimulator;

/**
 * Interface for the protocol simulator 
 *
 * @author Thomas Ejnefjäll
 * @version 2008-02-09
 */
public interface LayerSimulator
{
    /**
     * Sends a messages from the transport layer to the application layer
     * 
     * @param message Message to be sent
     */
    public void toApplicationLayer(String message);
    
    /**
     * Sends a segment from the transport layer to the network layer
     * 
     * @param segment
     */
    public void toNetworkLayer(Segment segment);
    
    /**
     * Starts a timer with the given time. If a timer is already active that
     * timer is first stopped and a new timer is started.
     * 
     * @param time Time in milliseconds until the timer expires
     */
    public void startTimer(int time);
    
    /**
     * Checks if there is a timer active
     * 
     * @return true if there is a timer active
     */
    public boolean isTimerActive();
    
    /**
     * Stops the timer if it is active. If no timer is active nothing is done 
     */
    public void resetTimer();
    
    /**
     * Prints text to the log
     * 
     * @param text Text that will we added to the log
     */
    public void print(String text);
}