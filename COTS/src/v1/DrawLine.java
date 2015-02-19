package v1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

@SuppressWarnings("serial")
public class DrawLine extends JFrame {
	// The canvas properties
	public static final int TITLE_CANVAS_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int TITLE_CANVAS_HEIGHT = 61;
	public static final int DIAGRAM_CANVAS_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int DIAGRAM_CANVAS_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - TITLE_CANVAS_HEIGHT - 63;
		
	// Title properties
	private final int TITLE_OFFSET = 165; // This offset is based on the font size and font type -> should be independent from the screen size
	
	// Diagram position properties
	private final int DIAGRAM_LIMIT_LEFT = 60;
	private final int DIAGRAM_LIMIT_TOP = 50;
	
	// Other properties
	private final int TEXT_OFFSET = 5;
	private final int MINIMIZED_CANVAS_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private final int MINIMIZED_CANVAS_HEIGHT = 600;
	
	// Canvas objects (based on private Class DrawCanvas)
	private DrawCanvas diagramCanvas;
	private DrawCanvas titleCanvas;
	
	// ArrayList / Vector to get all the Line2D object to be drawn -> will be drawn by the Overridden paintComponent()
	private ArrayList<COTSLine> lines;
	
	// Button to re-do the whole process
	private JButton resetButton;
	private final int RESET_BUTTON_WIDTH = 100;
	private final int RESET_BUTTON_HEIGHT = 50;
	
	// Constructor
	public DrawLine () {
		// Instantiating the ArrayList
		this.lines = new ArrayList<COTSLine>();
		
		// Maximizing the window
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(MINIMIZED_CANVAS_WIDTH, MINIMIZED_CANVAS_HEIGHT + 120));
		
		// Instantiating reset button
		this.resetButton = new JButton("Reset");
		this.resetButton.setSize(RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT);
		this.resetButton.setLocation(TITLE_CANVAS_WIDTH / 2 - RESET_BUTTON_WIDTH / 2, TITLE_CANVAS_HEIGHT + DIAGRAM_CANVAS_HEIGHT - RESET_BUTTON_HEIGHT - 30);
		this.add(this.resetButton);
		
		// Adding event listener to the button
		this.resetButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				// RESET
				closeFrame();
				COTSGenerator.COTSInit();
			}
		});
	}
	
	/*
	 * Define an inner class DrawCanvas which is a JPanel where the Line2D objects will be drawn
	 */
	
	private class DrawCanvas extends JPanel {
		private boolean title = false;
		
		public DrawCanvas (boolean title) {
			this.title = title;
		}
		
		//paintComponent is a built in function from Swing -> will be executed when DrawCanvas object is created
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE);		
			g.setColor(Color.BLACK);
			
			if (!this.title) {
				int horizontalTranslation = diagramHorizontalTranslation();
				int verticalTranslation = diagramVerticalTranslation();
				
				System.out.println(horizontalTranslation + "   " + verticalTranslation + "   ");
				
				// Translate the center of the graphics so that position adjusment for the whole diagram can be done easily
				g.translate(horizontalTranslation, -verticalTranslation);
	
				//Draw all lines
				for (COTSLine line: lines) {
					//System.out.println(line.getDashed());
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
					
					// Draw text context
					int curContext = line.getCurContext();
					ArrayList<Integer> prev = new ArrayList<Integer> ();
					
					// Use an ArrayList to sort the context -> HashSet is not "sortable"
					for (int i: line.getPrevContext()) {
						prev.add(i);
					}
					
					// Sorting
					Collections.sort(prev);
					
					// String containing all previous context
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
					
					// Final string
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
						y += TEXT_OFFSET;
					}
					
					// Set font properties
					g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 14));
					
					// Draw the textbox
					g.drawString(contextString, (int) x, (int) y);
				}
			}
			else {
				// Draw title
				//setBackground(Color.BLACK);
				g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 30));
				g.drawString("COTS Diagram Generator", TITLE_CANVAS_WIDTH / 2 - TITLE_OFFSET, 50);
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
					//System.out.println(i); // Debugging purpose
				}
				
				// Including that line current context itself
				l.addPrevContext(line.getCurContext());
			}
		}
	}
	
	// Get the x or y position of the leftmost, rightmost, topmost, or bottommost point
	// Purpose: readjusting the position of the whole diagram
	private int getLeftMostPoint () {
		int result = 0;
		for (COTSLine l: this.lines) {
			if (l.getX1() < result) {
				result = (int) l.getX1();
			}
		}
		
		return result;
	}
	
	private int getRightMostPoint () {
		int result = 0;
		for (COTSLine l: this.lines) {
			if (l.getX2() > result) {
				result = (int) l.getX2();
			}
		}
		
		return result;
	}
	
	private int getTopMostPoint () {
		int result = Toolkit.getDefaultToolkit().getScreenSize().height;
		for (COTSLine l: this.lines) {
			if (l.getY2() < result) {
				result = (int) l.getY2();
			}
		}
		
		return result;
	}
	
	private int getBottomMostPoint () {
		int result = 0;
		for (COTSLine l: this.lines) {
			if (l.getY1() > result) {
				result = (int) l.getY1();
			}
		}
		
		return result;
	}
	
	// Calculate how many pixels the translation needs to put the diagram in the middle horizontally
	private int diagramHorizontalTranslation () {
		int translation = 0;
		int diagramWidth = this.getRightMostPoint() - this.getLeftMostPoint();
		
		translation = (DIAGRAM_CANVAS_WIDTH / 2) - (diagramWidth / 2);
		
		// Prevent minus value
		if (translation < DIAGRAM_LIMIT_LEFT) {
			return DIAGRAM_LIMIT_LEFT;
		} else {
			return translation;
		}
	}
	
	// Calculate how many pixels the translation needs to put the diagram in the middle vertically
	private int diagramVerticalTranslation () {
		int translation = 0;
		int diagramHeight = this.getBottomMostPoint() - this.getTopMostPoint();
		
		translation = (DIAGRAM_CANVAS_HEIGHT / 2) - (diagramHeight / 2);
		if (this.getTopMostPoint() - translation < DIAGRAM_LIMIT_TOP) {
			return (this.getTopMostPoint() - DIAGRAM_LIMIT_TOP);
		} else {
			return translation;
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
		titleCanvas = new DrawCanvas(true);
		diagramCanvas = new DrawCanvas(false);
				
		// Set the canvas size
		titleCanvas.setPreferredSize(new Dimension(TITLE_CANVAS_WIDTH, TITLE_CANVAS_HEIGHT));
		diagramCanvas.setPreferredSize(new Dimension(DIAGRAM_CANVAS_WIDTH, DIAGRAM_CANVAS_HEIGHT));
				
		// getContentPane -> get the Container where components are usually added to
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
				
		// Adding the drawing canvas to the container
		cp.add(titleCanvas, BorderLayout.PAGE_START);
		cp.add(diagramCanvas, BorderLayout.CENTER);
				
		//Some Frame properties
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("COTS Diagram");
		this.setVisible(true);	
	}
	
	private void closeFrame () {
		super.dispose();
	}
}
