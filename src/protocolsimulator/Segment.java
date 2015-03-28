package protocolsimulator;
/**
 * Structure for messages that are sent between transport layers 
 * simulated on different host
 *
 * @author Thomas Ejnefjäll
 * @version 2008-02-14 
 */
public class Segment
{    
    public int seqNumber;
    public int ackNumber;
    public int checksum;
    public String payload = null;
    public String from;
    
    /**
     * Constructs a segment
     */
    public Segment()
    {
        this("", 0, 0, "");
    }
    /**
     * Constructs a segment
     *   
     * @param from Sender     
     * @param seqNumber Sequence number
     * @param ackNumber Acknowledge number
     * @param payload Message
     */
    public Segment(String from, int seqNumber, int ackNumber, String payload)
    {        
        this.from = from;
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.payload = payload;
        
        this.checksum = generateChecksum();
    }
    /**
     * Generates a checksum for the segment
     * 
     * @return Generated checksum
     */
    private int generateChecksum()
    {
         return seqNumber + ackNumber + payload.hashCode();
    }
    /**
     * Checks to see if segments is correct or corrupted
     * 
     * @return True is segment is correct
     */
    public boolean isCorrect()
    {
        return checksum == generateChecksum();        
    }   
    @Override
    public Segment clone()
    {
        return new Segment(from, seqNumber, ackNumber, payload);       
    }
    @Override
    public String toString()
    {
        return "From: " + from + " SeqNum: " + seqNumber + " AckNum: " + ackNumber + " Message: " + payload; 
    }
}