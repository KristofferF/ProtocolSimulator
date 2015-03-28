package protocolsimulator;

/**
 * Klassen simulerar transportlagret i en applikation
 * 
 * Andra lager skickar data till transportlagret via metoderna: 
 * <code>toTransportLayer(String)</code> när anropet kommer från applikationslagret och
 * <code>toTransportLayer(Segment)</code> när anropet kommer från nätverkslagret.<br /><br />
 * 
 * Transportlagret har en timer som kan användas.<br /><br />
 * <code>//startar en timer på 3 sekunder (3000 millisekunder)</code><br />
 * <code>mLayerSimulator.startTimer(3000)</code><br />
 * <code>//nollställer timern</code><br />
 * <code>mLayerSimulator.resetTimer()</code><br /><br />
 *  
 * När timerns tid har passerat anropas metoden: <code>timerInterrupt()</code><br /><br />
 * 
 * När transportlagret vill skicka data till andra lager används metoderna:
 * <code>mLayerSimulator.toApplicationLayer(String)</code> för att skicka till applikationslagret och
 * <code>mLayerSimulator.toNetworkLayer(Segment)</code> för att skicka till nätverkslagret<br /><br />
 *
 * Tanken är att ni ska lägga till metoder och variabler för att klassen ska 
 * implementera tillförlitlig överföring. Ni kan även lägga till andra klasser om ni vill.
 * Ni behöver dock inte göra några förändringar i de övriga klasserna. Ni behöver inte
 * ens titta igenom koden i dessa utan huvusaken är att ni förstår hur er klass
 * ska interagera med de andra.
 *
 * @author (name)
 * @version (version) 
 */
public class TransportLayer
{       
    private LayerSimulator mLayerSimulator = null;     
    private String mId;    
    private int mTimerValue, mWindowSize;
            
    /**
     * Constructs a TransportLayer
     * 
     * @param id Id for the host (A or B)
     * @param layers Simulates the layes above and below the Transport Layer
     * @param timerValue Timer value in ms (1000 ms = 1 sec)
     * @param windowSize The amount of unACK:ed packets we can transmit 
     */
    public TransportLayer(String id, LayerSimulator layers, int timerValue, int windowSize)
    {
    	System.out.println("Constructor");
    	mId = id;
    	mLayerSimulator = layers;
    	mTimerValue = timerValue;
    	mWindowSize = windowSize;
    }
    
    /**
     * Called from the Application Layer when a message is sent to an other host
     * 
     * @param message Message from the Application Layer
     */
    public void toTransportLayer(String message)
    { 
    	System.out.println("From app layer message: " + message);
    	Segment segment = new Segment("Kristoffer", 0, 0, "Hello there");
    	mLayerSimulator.print(segment.toString());
    	mLayerSimulator.toNetworkLayer(segment);
    } 
    
    /**
     * Called from the Network Layer when a segment arrives
     * 
     * @param segment Segment that arrives from the Network Layer
     */
    public void toTransportLayer(Segment segment)
    {
    	System.out.println("From net layer");
    	mLayerSimulator.print(segment.toString());
    	mLayerSimulator.toApplicationLayer(segment.payload);
    }
    
    /**
     * Called by the simulator when the specified time for the timer has passed
     */
    public void timerInterrupt()
    {
    	System.out.println("timerInterrupt");
    }
}