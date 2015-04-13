package protocolsimulator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

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
 * @author Kristoffer Freiholtz 8703165996
 * @version 1.0
 */
public class TransportLayer
{       
    private LayerSimulator mLayerSimulator = null;     
    private String mId;    
    private int mTimerValue, mWindowSize;
    private int mSequence = 0; 
    private final int mStandardAck = -1;
    private Deque<Segment> mBuffer = new ArrayDeque<Segment>();
    private Deque<Segment> mWindow = new ArrayDeque<Segment>();
    
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
    	Segment segment = new Segment(mId, mSequence, mStandardAck, message);
    	mBuffer.add(segment);
    	mLayerSimulator.print(mId + " received " + segment.payload + " from application layer");
    	mSequence++;
    	update();  	
    }
    
    
	/**
	 * update the transport layer
	 */
	private void update() {
		updateQueue();
    	updateTimer();    	
	}

	/**
	 * Updates the queues mBuffer and mWindow. Fills up mWindow to the choosen window size
	 */
	private void updateQueue() {
		if(mWindow.size() < mWindowSize && !mBuffer.isEmpty()){
    		Segment segment = mBuffer.remove();
			mWindow.add(segment);			 
    		send(segment);
    		mLayerSimulator.resetTimer();
    	}
	} 
	
	/**
	 * Send a segment to the network layer
	 * 
	 * @param segment The segment to be sent
	 */
	private void send(Segment segment) {  	   	
    	mLayerSimulator.print(mId + " sends " + segment.toString() + " to network layer");   	
    	mLayerSimulator.toNetworkLayer(segment);
	}
	
	/**
	 * Update and restarts the timer as long as the queues mBuffer and mWindow aren't empty
	 */
	private void updateTimer() {
		if(!mLayerSimulator.isTimerActive()){
			if(mBuffer.isEmpty() && mWindow.isEmpty()){
				mLayerSimulator.print("All packages sent and received succesfully!");
	    	}
			else{
				mLayerSimulator.print(mId + " starts timer: " + mTimerValue);
	    		mLayerSimulator.startTimer(mTimerValue);		
			}
		}
	}
    
    /**
     * Called from the Network Layer when a segment arrives
     * 
     * @param segment Segment that arrives from the Network Layer
     */
    public void toTransportLayer(Segment segment)
    {  	    	
    	if(segment.isCorrect()){
    		handleCorrectSegment(segment);   			   		    		
    	}
    	else{
    		mLayerSimulator.print(mId + " received corrupted package " + segment.toString());  		
    	}
    	update();
    }

	/**
	 * Handle the correct segment sent from the network layer
	 * 
	 * @param segment Segment that arrives from the Network Layer to be handled
	 */
	private void handleCorrectSegment(Segment segment) {
		mLayerSimulator.print(mId + " received correct package " + segment.toString());
		if(segment.payload.startsWith("ACK")){
			handleAck(segment);
		}
		else{	
			handlePackage(segment);	
		}
	}

    
	/**
	 * Handle correct received package from the network layer  
	 * 
	 * @param segment Segment that arrives from the Network Layer to be handled
	 */
	private void handlePackage(Segment segment) {
		mLayerSimulator.print(mId + " received message " + segment.payload);
		if(mSequence >= segment.seqNumber){
			mLayerSimulator.print(mId + " sends ACK " + segment.payload);
			Segment returnSegment = new Segment(mId, mStandardAck, segment.seqNumber, "ACK " + segment.payload.charAt(0));
			mLayerSimulator.toNetworkLayer(returnSegment);
			if(mSequence == segment.seqNumber){ 				
				mLayerSimulator.print(mId + " sends " + segment.payload + " to application layer");
				mLayerSimulator.toApplicationLayer(segment.payload); 
				mSequence++;
			}	
		} 			
		else{
			mLayerSimulator.print(mId + " throws away package " + segment.payload);
		}
	}

	/**
	 * Handle correct received acknowledgement from the network layer
	 * 
	 * @param segment Segment that arrives from the Network Layer to be handled
	 */
	private void handleAck(Segment segment) {
		mLayerSimulator.print(mId + " received ACK " + segment.payload); 
		if(!mWindow.isEmpty()){
			mLayerSimulator.print("Windows seqnumber: " + mWindow.getFirst().seqNumber + 
					"\nSegment ackNumber: " + segment.ackNumber +
					"\nWindows payload: " + mWindow.getFirst().payload);
			if(mWindow.getFirst().seqNumber == segment.ackNumber){
				mLayerSimulator.print(mWindow.getFirst().seqNumber + " " + mWindow.getFirst().payload + "removed");
				mWindow.removeFirst();
			}
		}
	}
    
    /**
     * Called by the simulator when the specified time for the timer has passed
     */
    public void timerInterrupt()
    {
    	mLayerSimulator.print("Timer Interrupt");
    	for (Segment segment : mWindow){
    		send(segment);
    	}
    	update();
    }
}