package v1;

import javax.swing.*;
import java.awt.*;

/**
 * The main application class of COTS Diagram Automatic Generator.
 * 
 * @author Edwin Candinegara & Kenrick
 * @version 1.0
 *
 */

@SuppressWarnings("serial")
public class COTSGenerator extends JFrame
{
	//private static JTextArea tracker;
	
	//Using a standard Java icon
    //private Icon optionIcon = UIManager.getIcon("FileView.computerIcon");
    
	public static void main (String[] args)
	{
		COTSInit();
	}
	
	public COTSGenerator()
    {
        // Make sure the program exits when the frame closes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("COTS Diagram Generator");
        setSize(500,300);
        
        //This will center the JFrame in the middle of the screen
        setLocationRelativeTo(null);
        
        /*
        //Using JTextArea to show clicks and responses
        tracker = new JTextArea("COTS Diagram Generator\n");
        add(tracker);
        setVisible(true); // By default, the textarea is VISIBLE on the JFrame
        */
        
        //Input dialog with a text field
        String input =  JOptionPane.showInputDialog(this, "Please note that current version of COTS Diagram Generator has a format for a given expression:\n"
        		+ "1. For each 2 operands, it has to be bounded by opening and closing parenthesis\n"
        		+ "2. Whitespaces between operands are fine\n"
        		+ "Example:\n"
        		+ "((1 -> 2) || 3)\n"
        		+ "((1 -> 2) || (3 -> 4))\n"
        		+ "(((1 -> 2 ) -> 3 ) -> 4)\n"
        		+ "((1 -> ((2 -> 3) || (4 -> 5))) -> 6)\n"
        		+ "\n",
        		"((1 -> ((2 -> 3) || (4 -> 5))) -> 6)");
        
        TrackResponse(input);
    }
    
    //Append the picked choice to the tracker JTextArea
    public void TrackResponse(String response)
    {	
        //showInputDialog method returns null if the dialog is exited
        //without an option being chosen
        if (response == null)
        {
            //tracker.append("You closed the dialog without any input\n");
        }
        else
        {
            //tracker.append("Input received: " + response + "\n");
            
            Parser parser = new Parser(response);
    		parser.parseExpression();
    		
    		DiagramGenerator diagramGenerator = new DiagramGenerator();
    		diagramGenerator.constructLines();
    		diagramGenerator.generateDiagram();
        }
    }
    
    public static void COTSInit () {
    	//Use the event dispatch thread for Swing components
	    EventQueue.invokeLater(new Runnable()
	    {
	    	public void run()
	    	{
	    		//create GUI frame
	    		new COTSGenerator();
	    	}
	    });
    }
}
