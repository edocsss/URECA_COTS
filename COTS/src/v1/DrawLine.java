package v1;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

@SuppressWarnings("serial")
public class DrawLine extends JFrame {
	// The canvas width and height
	public static final int CANVAS_WIDTH = 960;
	public static final int CANVAS_HEIGHT = 640;
	private static final int TEXT_OFFSET = 5;
	
	// A canvas object (based on private Class DrawCanvas)
	private DrawCanvas canvas;
	
	//ArrayList / Vector to get all the Line2D object to be drawn -> will be drawn by the Overridden paintComponent()
	private ArrayList<COTSLine> lines;
	
	//Constructor
	public DrawLine () {
		//Instantiating the ArrayList
		this.lines = new ArrayList<COTSLine>();
	}
	
	/*
	 * Define an inner class DrawCanvas which is a JPanel where the Line2D objects will be drawn
	 */
	
	private class DrawCanvas extends JPanel{
		//paintComponent is a built in function from Swing -> will be executed when DrawCanvas object is created
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE);		
			g.setColor(Color.BLACK);

			//Draw all lines
			for (COTSLine line: lines) {
				if (line.getDashed()) {
					// draw dashed line
					double lengthX = Math.abs(line.getX2() - line.getX1());
					double lengthY = Math.abs(line.getY2() - line.getY1());
					if (line.getY2() >= line.getY1()) {
						for (int i = 0; i < 10; i++) {
							g.drawLine(	(int) (line.getX1() + i * lengthX / 10.0),
										(int) (line.getY1() + i * lengthY / 10.0),
										(int) (line.getX2() - (9.5 - i) * lengthX / 10.0),
										(int) (line.getY2() - (9.5 - i) * lengthY / 10.0)
							);
						}
					} else {
						for (int i = 0; i < 10; i++) {
							g.drawLine(	(int) (line.getX2() + i * lengthX / 10.0),
										(int) (line.getY2() + i * lengthY / 10.0),
										(int) (line.getX1() - (9.5 - i) * lengthX / 10.0),
										(int) (line.getY1() - (9.5 - i) * lengthY / 10.0)
							);
						}
					}
				} else {
					// draw solid line
					g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				}
				
				// draw arrow
				if (line.getX1() == line.getX2()) {
					// vertical line
					if (line.getY2() > line.getY1()) {
						g.drawLine((int) line.getX1() - 5, (int) line.getY1() + 7, (int) line.getX2(), (int) line.getY1());
						g.drawLine((int) line.getX1() + 5, (int) line.getY1() + 7, (int) line.getX2(), (int) line.getY1());
					} else {
						g.drawLine((int) line.getX1() - 5, (int) line.getY2() + 7, (int) line.getX2(), (int) line.getY2());
						g.drawLine((int) line.getX1() + 5, (int) line.getY2() + 7, (int) line.getX2(), (int) line.getY2());
					}
				} else if (line.getY1() == line.getY2()) {
					// horizontal line
					if (line.getX2() > line.getX1()) {
						g.drawLine((int) line.getX2() - 7, (int) line.getY1() - 5, (int) line.getX2(), (int) line.getY2());
						g.drawLine((int) line.getX2() - 7, (int) line.getY1() + 5, (int) line.getX2(), (int) line.getY2());
					} else {
						g.drawLine((int) line.getX1() - 7, (int) line.getY1() - 5, (int) line.getX1(), (int) line.getY2());
						g.drawLine((int) line.getX1() - 7, (int) line.getY1() + 5, (int) line.getX1(), (int) line.getY2());
					}
				}
				
				// TODO Position adjustment
				// Draw text context
				int curContext = line.getCurContext();
				ArrayList<Integer> prev = new ArrayList<Integer> ();
				
				// Use an ArrayList to sort the context -> HashSet is not "sortable"
				for (int i: line.getPrevContext()) {
					prev.add(i);
				}
				
				Collections.sort(prev);
				String prevString = "";
				
				for (int i = 0; i < prev.size(); i++) {
					if (i == 0) {
						prevString += "{";
					}
					
					if (i == prev.size() - 1) {
						prevString += prev.get(i) + "}";
					} else {
						prevString += prev.get(i) + ", ";
					}
				}
				
				String contextString = "" + curContext + prevString;
				
				// Position of the textbox
				double x = (line.getX1() + line.getX2()) / 2;
				double y = (line.getY1() + line.getY2()) / 2;
				
				// TEXT POSITION ADJUSTMENT
				// Horizontal Line
				if (line.getY1() == line.getY2()) {
					x -= (contextString.length() / 2) * TEXT_OFFSET;
					y -= 10;
				} 
				// Vertical line
				// No need for text adjustment as the text box must have been in the middle of the line
				else if (line.getX1() == line.getX2()) {
					x += 10; 
				}
				
				// Set font properties
				g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 14));
				
				// Draw the textbox
				g.drawString(contextString, (int) x, (int) y);
				
				// Draw title
				g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 30));
				g.drawString("COTS Diagram Generator", CANVAS_WIDTH / 2 - 150, 50);
			}
		}
	}
	
	/*
	 *  Getter and setter method
	 */
	
	public ArrayList<COTSLine> getLines () {
		return this.lines;
	}
	
	public void addLine (COTSLine l) {
		this.setLineContext(l);
		
		// Debugging purpose
		System.out.println("Start point:");
		System.out.println("X: " + l.x1);
		System.out.println("Y: " + l.y1);
		
		System.out.println("End point:");
		System.out.println("X: " + l.x2);
		System.out.println("Y: " + l.y2);
		
		System.out.println("Cur: " + l.getCurContext());
		System.out.print("Prev: ");
		for (int i: l.getPrevContext()) {
			System.out.print(i + " ");
		}
		
		System.out.println("\n");
		
		this.lines.add(l);
	}
	
	public void removeAllLines () {
		this.lines.clear();
	}
	
	// Adding context
	private void setLineContext (COTSLine l) {
		if (this.lines.size() == 0) return;
		for (COTSLine line: this.lines) {
			
			if (line.getP2().equals(l.getP1())) {				
				for (int i: line.getPrevContext()) {
					l.addPrevContext(i);
					System.out.println(i); // Debugging purpose
				}
				
				l.addPrevContext(line.getCurContext());
			}
		}
	}
	
	/*
	 * Initialization of drawing
	 * Call initDraw() IF AND ONLY IF all lines to be drawn has been added to the attribute lines
	 * Because creating a new DrawCanvas() will call the paintComponent() -> this paintComponent() will draw the lines to the canvas
	 * immediately
	 */
	
	public void initDraw () {
		// Create a new canvas object
		canvas = new DrawCanvas();
				
		// Set the canvas size -> DrawCanvas extends JPanel + JPanel extends JComponent -> JComponent has the setPreferredSize method
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
				
		// getContentPane -> get the Container where components are usually added to
		Container cp = getContentPane();
				
		// Adding the drawing canvas to the container
		cp.add(canvas);
				
		//Some Frame properties
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setTitle("COTS Diagram");
		this.setVisible(true);
	}
}
