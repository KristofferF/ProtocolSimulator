package protocolsimulator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Main GUI for the simulator
 *
 * @author Thomas Ejnefjäll
 * @version 2007-11-23
 */
public class GUI extends JFrame
{
    private static final long serialVersionUID = -8194582744105531571L;
    private Screen mScreen;
    private JTextArea mLogg;
    private ControlPanel mSettings;
    private static GUI mInstance = new GUI();
    
    /**
     * Constructs the GUI
     */
    private GUI()
    {
        super(".: Transport Layer Protocol simulator :.");        
        this.initializeGUI();
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);   
    }
    /**
     * Gets the unique instance of the GUI
     * 
     * @return Unique instance of the GUI
     */
    public static GUI getInstance()
    {
        return mInstance;        
    }
    /**
     * Adds text to the log
     * 
     * @param text The text to be added to the log
     */
    public void addToLogg(String text)
    {
        mLogg.setText(text + "\n" + mLogg.getText());               
    }
    /**
     * Clears the log
     */
    public void clearLogg()
    {
        mLogg.setText("");        
    }
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
        mScreen.animate(messagesToSend, receivedMessages, events, currentTime);        
    }
    private void initializeGUI()
    {        
        mScreen = new Screen();
        mLogg = new JTextArea();
        mLogg.setRows(9);
        mLogg.setEditable(false);
        mSettings = new ControlPanel();
        
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(mLogg);
        
        contentPane.add(BorderLayout.PAGE_START, mScreen);
        contentPane.add(BorderLayout.CENTER, scrollPane);
        contentPane.add(BorderLayout.LINE_END, mSettings);
    }    
}