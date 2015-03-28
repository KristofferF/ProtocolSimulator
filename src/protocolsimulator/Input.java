package protocolsimulator;

/**
 * Encapsulates all the user input for the simulation
 *
 * @author Thomas Ejnefjäll
 * @version 2007-11-20
 */
public class Input
{
    public int lossProbability, corruptionProbability, numberOfMessages, timerValue, windowSize;     
    
    /**
     * Constructs an Input
     * 
     * @param timerValue Timer value to use in the simulation
     * @param lossProbability Probability for loss of segments
     * @param corruptionProbability Probability for corruption of segments
     * @param numberOfMessages Number of messages for the simulation
     * @param windowSize Window size to use in the simulation
     */
    public Input(int timerValue, int lossProbability, int corruptionProbability, int numberOfMessages, int windowSize)
    {
        this.timerValue = timerValue;
        this.lossProbability = lossProbability;
        this.corruptionProbability = corruptionProbability;
        this.numberOfMessages = numberOfMessages;       
        this.windowSize = windowSize;
    }
}