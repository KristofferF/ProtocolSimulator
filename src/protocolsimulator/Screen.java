package protocolsimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The Screen where the layers and animation of the messages are shown
 *
 * @author Thomas Ejnefjäll
 * @version 2008-02-09
 */
public class Screen extends JPanel
{
    private static final long serialVersionUID = 475467605642627134L;
    private List<String> mMessagesToSend, mReceivedMessages;
    private List<Event> mEvents;    
    private int leftX = 100, y = 30, rightX = 500;
    private long mCurrentTime;
    
    /**
     * Constructs a Screen
     */
    public Screen() {}
    /**
     * Animates messages and packets 
     * 
     * @param messagesToSend Messages waiting to be sent from application layer
     * @param receivedMessages Messages received at application layer
     * @param events Packets in transit to be animated
     * @param currentTime Time for animation
     */
    public void animate(List<String> messagesToSend, List<String> receivedMessages, List<Event> events, long currentTime)
    {
        this.mMessagesToSend = messagesToSend;
        this.mReceivedMessages = receivedMessages;
        this.mEvents = events;
        this.mCurrentTime = currentTime;
        
        this.repaint();
    }
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(720, 200);
    }
    @Override
    public void paintComponent(Graphics g)
    {
        this.clearBackground(g);
        this.drawBackground(g);
        this.animate(g);
    }
    /**
     * Clears the screen background
     * 
     * @param g The graphics
     */ 
    private void clearBackground(Graphics g) 
    {        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());     
    }
    /**
     * Draws the background 
     * 
     * @param g The graphics
     */
    private void drawBackground(Graphics g)
    {        
        g.setColor(Color.DARK_GRAY);
        
        g.drawString("A", leftX + 55, y - 5);
        g.drawString("B", rightX + 55, y - 5);
        
        //Draw legend
        this.drawLegend(g);        
        
        //Draw name of layers and lines between them
        this.drawTextLayers(g, leftX, y);
        this.drawTextLayers(g, rightX, y);
        
        //Draw lines around and between A and B
        g.drawLine(leftX + 120, y, leftX + 120, y + 5 * 20);
        g.drawLine(rightX, y, rightX, y + 5 * 20);
        g.drawLine(leftX + 120, y + 5 * 20, rightX, y + 5 * 20);
        
        g.drawLine(leftX, y + 20, leftX, y + 7 * 20);
        g.drawLine(rightX + 120, y + 20, rightX + 120, y + 7 * 20);
        g.drawLine(leftX, y + 7 * 20, rightX + 120, y + 7 * 20);
        
        g.drawString("Unrealiable channel", leftX + 200, y + 8 * 20);  
        
        //Draw left application layer containers
        g.drawString("To send", leftX - 80, y - 5);
        g.drawLine(leftX - 80, y, leftX, y);
        g.drawLine(leftX - 80, y, leftX - 80, y + 7 * 20);
        g.drawLine(leftX - 80, y + 7 * 20, leftX - 20, y + 7 * 20);
        g.drawLine(leftX - 20, y + 20, leftX - 20, y + 7 * 20);
        g.drawLine(leftX - 20, y + 20, leftX, y + 20);
        
        //Draw right application layer containers
        g.drawString("Received", rightX + 140, y - 5);
        g.drawLine(rightX + 120, y, rightX + 200, y);
        g.drawLine(rightX + 200, y, rightX + 200, y + 7 * 20);
        g.drawLine(rightX + 140, y + 7 * 20, rightX + 200, y + 7 * 20);
        g.drawLine(rightX + 140, y + 20, rightX + 140, y + 7 * 20);
        g.drawLine(rightX + 120, y + 20, rightX + 140, y + 20);        
    }
    private void drawTextLayers(Graphics g, int x, int y)
    {
        String[] layers = new String[] {"Application Layer", "Transport Layer", "Network Layer", "Data Link Layer", "Physical Layer"};
        
        for(int i = 0; i < layers.length; i ++)
        {
            g.drawLine(x, y + i * 20, x + 120, y + i * 20);
            g.drawString(layers[i], x + 10, y + (i + 1) * 20 - 5);               
        }          
    }
    private void drawLegend(Graphics g)
    {
        int legendX = leftX + 200, legendY = y + 15;
        
        g.drawString("Legend", legendX, legendY);
        g.setColor(Color.GREEN);
        g.fillRect(legendX, legendY + 5, 50, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Ok", legendX + 60, legendY + 20);
        g.setColor(Color.RED);
        g.fillRect(legendX, legendY + 25, 50, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Corrupted", legendX + 60, legendY + 40);        
    }
    private void animate(Graphics g)
    {
        if(this.mMessagesToSend != null)
        {
            this.animateMessages(g, mMessagesToSend, leftX, false);            
        }
        if(this.mReceivedMessages != null)
        {
            this.animateMessages(g, mReceivedMessages, rightX + 220, true);
        }
        if(this.mEvents != null)
        {
            this.animateNetworkPackets(g);            
        }
    }
    private void animateMessages(Graphics g, List<String> messages, int x, boolean reverse)
    {          
        if(reverse)
        {
            List<String> reverseMessages = new ArrayList<String>();
            
            for(String s : messages)
            {
                reverseMessages.add(0, s);
            }
            messages = reverseMessages;
        }
        
        for(int i = 0; i < messages.size(); i ++)
        {
            if(i == 6 && messages.size() > 7)
            {
                g.drawString("...", x - 70, 25 + (i + 1) * 20);             
                break;
            }
            else
            {
                g.drawString(messages.get(i), x - 70, 25 + (i + 1) * 20);               
            }               
        }                
    }
    private void animateNetworkPackets(Graphics g)
    {
        long timeBetweenHosts = 1000;
        int distance = rightX + 70 - leftX;
        Color color = Color.YELLOW;
                
        for(Event e : mEvents)
        {              
            double timeToArrival = e.eventTime - mCurrentTime;
            double shareOfDistance = timeToArrival / timeBetweenHosts;            
            int distanceMade = (int)(distance - (shareOfDistance * distance));
            int packetX = 0, packetY = 0;
            boolean animate = true;
            
            if(e.extendedSegment.segment.from.equals("A"))
            {
                packetX = leftX + distanceMade; 
                packetY = y + 100;
                color = Color.GREEN;
            }
            else if(e.extendedSegment.segment.from.equals("B"))
            {
                packetX = rightX + 70 - distanceMade; 
                packetY = y + 120;
                color = Color.GREEN;
            }            
            if(e.extendedSegment.isCorrupted)
            {
                color = Color.RED;
            }
            animate = e.eventTime < mCurrentTime + timeBetweenHosts && !(e.extendedSegment.isLost && packetX > leftX + 220);
            
            if(animate) 
            {
                this.drawNetworkPacket(g, packetX, packetY, color, e.extendedSegment.segment.payload);
            }            
        }
    }
    private void drawNetworkPacket(Graphics g, int x, int y, Color color, String text)
    {
        g.setColor(color);
        g.fillRect(x, y, 50, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 10, y + 15);        
    }
}