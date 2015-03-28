package protocolsimulator;

/**
 * Extra information about the segment
 *
 * @author Thomas Ejnefjäll
 * @version 2008-02-14
 */
public class ExtendedSegment
{    
    public boolean isCorrupted, isLost;
    public Segment segment; 
    
    /**
     * COnstructs a ExtendedSegment
     *     
     * @param segment The segment
     * @param isCorrupted Whether the segment will be corrupted
     * @param isLost Whether the segment will be lost
     */
    public ExtendedSegment(Segment segment, boolean isCorrupted, boolean isLost)
    {        
        this.segment = segment;
        this.isCorrupted = isCorrupted;
        this.isLost = isLost;
    }
}