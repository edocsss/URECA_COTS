package v1;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
public class DrawLine extends JFrame {
	// The canvas width and height
	public static final int CANVAS_WIDTH = 640;
	public static final int CANVAS_HEIGHT = 480;
	
	// A canvas object (based on private Class DrawCanvas)
	private DrawCanvas canvas;
	
	//ArrayList / Vector to get all the Line2D object to be drawn -> will be drawn by the Overridden paintComponent()
	private ArrayList<Line2D.Double> lines;
	
	//Constructor
	public DrawLine () {
		//Instantiating the ArrayList
		this.lines = new ArrayList<Line2D.Double>();
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
			for (Line2D.Double line: lines) {
				g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				
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
				} else if (line.getY1() == line.getY2()){
					// horizontal line
					if (line.getX2() > line.getX1()) {
						g.drawLine((int) line.getX2() - 7, (int) line.getY1() - 5, (int) line.getX2(), (int) line.getY2());
						g.drawLine((int) line.getX2() - 7, (int) line.getY1() + 5, (int) line.getX2(), (int) line.getY2());
					} else {
						g.drawLine((int) line.getX1() - 7, (int) line.getY1() - 5, (int) line.getX1(), (int) line.getY2());
						g.drawLine((int) line.getX1() - 7, (int) line.getY1() + 5, (int) line.getX1(), (int) line.getY2());
					}
				}
			}
		}
	}
	
	/*
	 *  Getter and setter method
	 */
	
	public ArrayList<Line2D.Double> getLines () {
		return this.lines;
	}
	
	public void addLine (Line2D.Double l) {
		this.lines.add(l);
	}
	
	public void removeAllLines () {
		this.lines.clear();
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