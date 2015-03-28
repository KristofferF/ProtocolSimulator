package protocolsimulator;

/**
 * Events in the simulation
 *
 * @author Thomas Ejnefjäll
 * @version 2007-11-20
 */
public class Event
{
    public enum EventType {ApplicationLayer, NetworkLayer, TimerInterrupt};
    public EventType eventType;
    public long eventTime;
    public ExtendedSegment extendedSegment;
    
    /**
     * Constructs an event 
     * 
     * @param eventType Type of event
     * @param eventTime Time for the event
     * @param extendedSegment Information about segment
     */
    public Event(EventType eventType, long eventTime, ExtendedSegment extendedSegment)
    {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.extendedSegment = extendedSegment;
    }
    @Override
    public String toString()
    {
        return "eventType: " + eventType + " eventTime: " + eventTime;
    }
}
