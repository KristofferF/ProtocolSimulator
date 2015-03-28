package protocolsimulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Panel for all the user input for the simulation
 *
 * @author Thomas Ejnefjäll
 * @version 2007-11-20
 */
public class ControlPanel extends JPanel
{
    private static final long serialVersionUID = 8851125516496197874L;
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private JButton start;
    private JTextField numberOfMessagesField, timerField, windowSizeField, corruptionField, lossField;
    private List<JComponent> disableableComponets = new ArrayList<JComponent>(); 
    private Simulator mSimulator = null;
    
    /**
     * Constructs a ControlPanel
     */
    public ControlPanel()
    {
        this.initializeGUI();        
    }
    private void initializeGUI()
    {
        this.setBackground(Color.WHITE);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
        this.add(this.makeSettingsPanel());        
        this.add(this.makeButtonPanel());                
    }
    private JPanel makeSettingsPanel()
    {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(this.createBoarder("Inst�llningar"));        
        settingsPanel.setLayout(new BorderLayout());
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setLayout(new GridLayout(0, 2));        
        numberOfMessagesField = new JTextField("5");       
        numberOfMessagesField.setAlignmentX(RIGHT_ALIGNMENT);
        optionsPanel.add(new JLabel("Antal meddelanden "));
        optionsPanel.add(numberOfMessagesField);
                  
        timerField = new JTextField("3000");        
        optionsPanel.add(new JLabel("Timer (ms) "));
        optionsPanel.add(timerField);
        
        windowSizeField = new JTextField("2");
        optionsPanel.add(new JLabel("F�nsterstorlek "));
        optionsPanel.add(windowSizeField);        
                
        JLabel probability = new JLabel("Sannolikhet f�r...");
        
        JPanel probabilityPanel = new JPanel();
        probabilityPanel.setBackground(Color.WHITE);
        probabilityPanel.setLayout(new GridLayout(0, 3));             
        corruptionField = new JTextField("20");
        lossField = new JTextField("20");
        probabilityPanel.add(new JLabel("...korruption"));
        probabilityPanel.add(corruptionField);
        probabilityPanel.add(new JLabel("% (0 - 99)"));
        probabilityPanel.add(new JLabel("...f�rlust"));
        probabilityPanel.add(lossField);
        probabilityPanel.add(new JLabel("% (0 - 99)"));
        
        disableableComponets.add(numberOfMessagesField);
        disableableComponets.add(corruptionField);
        disableableComponets.add(lossField);
        disableableComponets.add(timerField);
        disableableComponets.add(windowSizeField);
        
        settingsPanel.add(optionsPanel, BorderLayout.PAGE_START);
        settingsPanel.add(probability, BorderLayout.LINE_START);
        settingsPanel.add(probabilityPanel, BorderLayout.PAGE_END);
        
        this.setFont(optionsPanel);
        this.setFont(settingsPanel);
        this.setFont(probabilityPanel);
               
        return settingsPanel;
    }    
    private JPanel makeButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        buttonPanel.setBackground(Color.WHITE);
        
        start = new JButton("Start");
        JButton stop = new JButton("Stopp");
        
        disableableComponets.add(start);
        
        start.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                start();
            }
        });        
        stop.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                stop();
            }
        });
        
        buttonPanel.add(start);
        buttonPanel.add(stop);
        
        this.setFont(buttonPanel);
        
        return buttonPanel;
    }
    private void start()
    {       
        Input input = null;
        
        try
        {
            input = this.validateInput();
            
            this.setEnabledComponents(false);
            
            mSimulator = new Simulator(input);                    
            mSimulator.startSimulation();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());            
        }        
    }
    private void setEnabledComponents(boolean enabled)
    {
        if(mSimulator != null)
        {
            mSimulator.stopSimulation();
            mSimulator = null;
        }
        
        for(JComponent c : disableableComponets)
        {
            c.setEnabled(enabled);
        }
    }    
    private void stop()
    {
        this.setEnabledComponents(true);                
    }
    private Input validateInput() throws Exception
    {
        int lossProbability = this.convertToInt(lossField.getText(), 0, 99);
        int corruptionProbability = this.convertToInt(corruptionField.getText(), 0, 99);
        int numberOfMessages = this.convertToInt(numberOfMessagesField.getText(), 1, Integer.MAX_VALUE);
        int timerValue = this.convertToInt(timerField.getText(), 0, Integer.MAX_VALUE);
        int windowSize = this.convertToInt(windowSizeField.getText(), 1, Integer.MAX_VALUE);
                                
        return new Input(timerValue, lossProbability, corruptionProbability, numberOfMessages, windowSize);
    }
    private int convertToInt(String text, int min, int max) throws Exception
    {
        int value = 0;
        
        try
        {
            value = Integer.parseInt(text);
            
            if(value < min || value > max)
            {
                throw new Exception("ogiltigt tal");                
            }
        }
        catch(NumberFormatException e)
        {
            throw new Exception("ogiltigt v�rde, endast siffror kan anv�ndas");                        
        }
        return value;        
    }
    private Border createBoarder(String title)
    {
        Border grayLine = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        return BorderFactory.createTitledBorder(grayLine, title, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, font);      
    }
    private void setFont(JPanel panel)
    {
        for(java.awt.Component c : panel.getComponents())
        {
            c.setFont(font);            
        }        
    }
}