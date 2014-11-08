package ExpressionParser;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

public class OperationGroup 
{
	/*
	 * Assumption: the first operand will always be drawn in the first dimension (EAST) and the second operand
	 * 			   in the second dimension (NORTH)
	 */
	private int firstOperand;
	private int secondOperand;
	private int id;
	private String operator;
	
	public OperationGroup (int firstOperand, int secondOperand, int id, String operator)
	{
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
		this.id = id;
		this.operator = operator;
	}
	
	/**
	 * How it works:
	 * <p>For every call, it checks whether it is a concurrent operation. If it is, then the process of drawing can be done
	 * as follows:
	 * <ul>
	 * <li>
	 * For every line drawn in Site 1 (meaning that it is the first operand and it is drawn to the right), draw the site 2 line
	 * as well (the line going upward).
	 * <ol> 
	 * 	<li>To know how many lines to draw, look for the depth of the second operand (Site 2 operation)</li>
	 * 	<li>If the depth is n, then it means there are n causal relation in Site 2 (REMEMBER: this is a 2D COTS)</li>
	 * </ol>
	 * </li>
	 * <li>
	 * After that, the coordPointer will go back to the origin where the concurrency happened before.
	 * </li>
	 * <li>
	 * From there, it will start drawing the other site with the same ways as before. The only difference is just the coordPointer
	 * does not trace back after drawing the last transformed line from Site 1.
	 * </li>
	 * 
	 * <p>
	 * If it is a causal relation:
	 * <ul>
	 * <li>
	 * Check whether the first operand is another OperationGroup or a single operand
	 * </li>
	 * <li>
	 * If it is an OperationGroup, do a recursive call
	 * </li>
	 * <li>
	 * If it is a single operand, draw the line to the right (we assume the first operand is always in site 1) and DO NOT MOVE
	 * BACK the coordPointer as this is a pure causality relation
	 * </li>
	 * <li>
	 * Check the second operand, and do the same thing
	 * </li>
	 * </ul>
	 * </p>
	 * <p>
	 * NOTE: It is impossible to find both first and second operand have a OperationGroup object!! This is a 2D COTS Generator!!
	 * </p>
	 * 
	 * <p>
	 * <b>IMPORTANT NOTE!!<b>
	 * The idea of recursively calling this method is to get the smallest operand possible, which is a single operand!!
	 * After this SINGLE OPERAND is found, start drawing the lines.
	 * </p>
	 * 
	 * @param d					A DrawLine object, to add the lines
	 * @param operationGroups	All OperationGroup objects -> to get an object based on the ID (to call a recursive method)
	 * @param coordPointer		The pointer in the coordinate system
	 * @param concurrency		Whether the current processed OperationGroup is a concurrent operation
	 * @param prevConcurrent	Whether the current OperationGroup is CALLED from a concurrent OperationGroup object
	 * 							If we know that the current OperationGroup is from a CONCURRENT OperationGroup, we can
	 * 							draw the other site part together with this site
	 * @param n					An integer indicating how many lines needed to be drawn for the other site
	 * @param north				Where should the line is drawn (up or right -> indicating whether it is site 1 or 2 actually)
	 */
	public void generateDiagram (DrawLine d, ArrayList<OperationGroup> operationGroups, Point2D.Double coordPointer, 
								 boolean concurrency, boolean prevConcurrent, int n, boolean north)
	{
		// Temporary variable declaration
		OperationGroup og;
		Point2D.Double endPoint;
		Line2D.Double l;
		double[] pointerMovement = new double[] {0.0, 0.0}; // Keep track how far the coordPointer has moved
		int numLines = 1; // This keeps track the number of operations from the other site need to be drawn when doing one site
						  // If it is concurrent, at least there is 1 line from other site to be drawn
		double offsetX, offsetY; // These two variables will be different depending on which site the line is drawn
		double traceBackX, traceBackY;
				
		// If it is a concurrent operation
		if (concurrency)
		{
			// Check whether second operand is another OperationGroup
			if (this.secondOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.secondOperand);
				
				// Get the number of lines need to be drawn in Site 1
				numLines = og.getDepth(operationGroups, 0);
			}
			
			// If the first operand is also a OperationGroup
			if (this.firstOperand < 0)
			{
				// Keep track where the coordPointer was before the concurrency happened (the interjunction between the two sites)
				traceBackX = coordPointer.x;
				traceBackY = coordPointer.y;
				
				// Retrieve the object
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
				
				// Recursive call according to the type of operator
				// It is impossible to get "||" as this is a 2D COTS
				if (og.getOperator().equalsIgnoreCase("||"))
				{
					// prevConcurrency = true because the OperationGroup calls generateDiagram() from a OperationGroup with concurrent operator
					// north = false because it is still the first operand, it means it works on the first site (draw to the right)
					og.generateDiagram(d, operationGroups, coordPointer, true, true, numLines, false);
				}
				else if (og.getOperator().equalsIgnoreCase("->"))
				{
					og.generateDiagram(d, operationGroups, coordPointer, false, true, numLines, false);
				}
				
				// Move back the pointer into the place where the interjunction of the two sites before the concurrency happened
				coordPointer.setLocation(traceBackX, traceBackY);
			}
			// If the first operand is a single operand, draw directly 
			else if (this.firstOperand > 0)
			{
				// The first operand of a concurrent operation must be in the first site
				// Draw to the right -> change the X value
				offsetX = DiagramGenerator.OFFSET_X;
				offsetY = 0.0;
				
				// Decide where the line will end
				endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
				
				// Construct the line
				l = new Line2D.Double(coordPointer, endPoint);
				
				// Add the line to the list
				d.addLine(l);
				
				// Update the coordPointer for the purpose of next line creation
				coordPointer.setLocation(endPoint);
				
				// Keep track how far the pointer has moved
				pointerMovement[0] += offsetX;
				pointerMovement[1] += offsetY;
				
				// Update the offset
				// This offset will be used to draw the OTHER SITE lines (in this case is Site 2 and therefore it
				// will be drawn upwards)
				offsetX = 0.0;
				offsetY = DiagramGenerator.OFFSET_Y;
				
				// Construct and add the lines of the SITE 2
				for (int i = 0; i < numLines; i++)
				{
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					d.addLine(l);
					
					// Update the pointer and how far it has moved
					coordPointer.setLocation(endPoint);
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
				}
				
				// Trace back the coordPointer so that it goes back to where the concurrency starts
				// It is to prepare to draw the lines of SITE 2
				coordPointer.setLocation(coordPointer.x - pointerMovement[0], coordPointer.y - pointerMovement[1]);
			}
						
			// Reset the number of lines need to be drawn (this is for SITE 1 now)
			numLines = 1;
			
			// Update the number of lines from SITE 1 which needs to be drawn while drawing the lines for SITE 2
			if (this.firstOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
				numLines = og.getDepth(operationGroups, 0);
			}
			
			// If the second operand is also a OperationGroup
			if (this.secondOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.secondOperand);
				
				if (og.getOperator().equalsIgnoreCase("||"))
				{
					// prevConcurrency = true because the OperationGroup calls generateDiagram() from a OperationGroup with concurrent operator
					// north = true because it is now the second operand, it means it works on the second site (draw upwards)
					og.generateDiagram(d, operationGroups, coordPointer, true, true, numLines, true);
				}
				else if (og.getOperator().equalsIgnoreCase("->"))
				{
					og.generateDiagram(d, operationGroups, coordPointer, false, true, numLines, true);
				}
			}
			// When the second operand is now only a SINGLE operand
			else if (this.secondOperand > 0)
			{
				// Since this is SITE 2, the drawing of SITE 2's own operation is UPWARDS
				// That's why the Y is changed
				offsetX = 0.0;
				offsetY = DiagramGenerator.OFFSET_Y;
				
				// Determine where the line ends and construct the line object
				endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
				l = new Line2D.Double(coordPointer, endPoint);
				
				// Add the line to the list of lines to be drawn
				d.addLine(l);
				
				// Update the coordPointer location
				coordPointer.setLocation(endPoint);
				
				// Keep track how far the coordPointer has moved
				pointerMovement[0] += offsetX;
				pointerMovement[1] += offsetY;
				
				// Update the offset to prepare drawing the other site's lines (SITE 1)
				offsetX = DiagramGenerator.OFFSET_X;
				offsetY = 0.0;
				
				// Actually draws and add the lines from SITE 1
				for (int i = 0; i < numLines; i++)
				{
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					d.addLine(l);
					
					// Update and prepare the coordPointer for the next drawing 
					coordPointer.setLocation(endPoint);
					
					// Keep track how far coordPointer has moved
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
				}
			}
		}
		else
		{
			// In case it is a causal relation, but it is called from a concurrent GroupOperation previously
			// It means that for each line drawn, it must draw the other site's lines
			if (prevConcurrent)
			{
				// Recursive call -> to get the smallest operand
				if (this.firstOperand < 0)
				{
					og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
					
					// Impossible to get the || as this is a 2D
					if (og.getOperator().equalsIgnoreCase("||"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, true, prevConcurrent, n, north);
					}
					else if (og.getOperator().equalsIgnoreCase("->"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, false, prevConcurrent, n, north);
					}
				}
				// If it is already the smallest operand, draw the line 
				else if (this.firstOperand > 0)
				{
					// As this part of function (the causal relation part) can be called either by SITE 1 or SITE 2
					// We must determine which direction the line will be drawn
					
					// If drawing for SITE 2
					if (north)
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
					}
					// Drawing for SITE 1
					else
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
					}
					
					// Construct THAT SITE's line
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					
					// Add the line
					d.addLine(l);
					coordPointer.setLocation(endPoint);
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
					
					// Now, preparation for THE OTHER SITE's line drawing (it must be in the reverse direction than the current SITE)
					// That's why below is the swapping of the offset
					if (north)
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
						
					}
					else
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
					}
										
					// Draw the lines from the other site
					for (int i = 0; i < n; i++)
					{
						endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
						l = new Line2D.Double(coordPointer, endPoint);
						d.addLine(l);
						
						coordPointer.setLocation(endPoint);
						pointerMovement[0] += offsetX;
						pointerMovement[1] += offsetY;
					}
					
					// Trace back the coordPointer to the original place if and only if it is in the SITE 1
					// This goes back to the place before the line from SITE 2 is drawn when drawing SITE 1's lines
					if (!north)
					{
						coordPointer.setLocation(coordPointer.x, coordPointer.y - pointerMovement[1]);
						pointerMovement[1] = 0.0;
					}
				}
				
				// If the second operand is a OperationGroup
				if (this.secondOperand < 0)
				{
					og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
					
					// In the case of SITE 2 causal relation with previous concurrency = true
					// The checking is done as follows
					// 1. After the first operand is done, the last pointer is still on the tip of the lines created for SITE 1 (the transformed line)
					// 2. Therefore, we need to trace back to the point where the line should continue for SITE 2
					// 3. THIS ONLY WORKS WHEN DRAWING UPWARDS!!
					if (north)
					{
						// n * OFFSET_X means that there are n lines to be drawn from the other site, and per line has the width
						// of OFFSET_X
						coordPointer.setLocation(coordPointer.x - n * DiagramGenerator.OFFSET_X, coordPointer.y);
					}
					
					if (og.getOperator().equalsIgnoreCase("||"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, true, prevConcurrent, n, north);
					}
					else if (og.getOperator().equalsIgnoreCase("->"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, false, prevConcurrent, n, north);
					}
				}
				else if (this.secondOperand > 0)
				{
					// Setting up the direction of drawing
					if (north)
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
						
						// Same reason as above, however this one is a single operand
						coordPointer.setLocation(coordPointer.x - n * DiagramGenerator.OFFSET_X, coordPointer.y);
					}
					else
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
					}
					
					// Construct line, add line, keep track of coordPointer
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					
					d.addLine(l);
					coordPointer.setLocation(endPoint);
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
					
					// Setting up for drawing the other SITE's lines
					if (north)
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
						
					}
					else
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
					}
					
					// Actually drawing the other SITE's line
					for (int i = 0; i < n; i++)
					{
						endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
						l = new Line2D.Double(coordPointer, endPoint);
						d.addLine(l);
						
						coordPointer.setLocation(endPoint);
						pointerMovement[0] += offsetX;
						pointerMovement[1] += offsetY;
					}
					
					// IF IT IS SITE 1: TRANSLATE BACK THE COORDPOINTER TO THE CORRECT POSITION AFTER DRAWING THE OTHER SITE'S
					// LINES (MOVE ALONG AXIS Y ONLY) because the drawing of SITE 1's original line is to the right
					// After drawing the other site's lines, the point is not in the main line where the original line is being
					// drawn
					if (!north)
					{
						coordPointer.setLocation(coordPointer.x, coordPointer.y - pointerMovement[1]);
						pointerMovement[1] = 0.0;
					}
				}
			}
			// If it is just a usual causal relation (no concurrency previously)
			else
			{
				// Getting the smallest operand
				if (this.firstOperand < 0)
				{
					og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
					if (og.getOperator().equalsIgnoreCase("||"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, true, prevConcurrent, n, north);
					}
					else if (og.getOperator().equalsIgnoreCase("->"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, false, prevConcurrent, n, north);
					}
				}
				// If it is the smallest operand, then start drawing ONLY THIS SITE!! (because there is no concurrency)
				else if (this.firstOperand > 0)
				{
					// Setting up the direction of drawing
					if (north)
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
					}
					else
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
					}
					
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					
					d.addLine(l);
					coordPointer.setLocation(endPoint);
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
				}
				
				// Checking for second operand
				if (this.secondOperand < 0)
				{
					og = OperationGroupManager.getOperationGroupById(operationGroups, this.secondOperand);
					if (og.getOperator().equalsIgnoreCase("||"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, true, true, n, north);
					}
					else if (og.getOperator().equalsIgnoreCase("->"))
					{
						og.generateDiagram(d, operationGroups, coordPointer, false, true, n, north);
					}
				}
				// Drawing the second operand
				else if (this.secondOperand > 0)
				{
					if (north)
					{
						offsetX = 0.0;
						offsetY = DiagramGenerator.OFFSET_Y;
					}
					else
					{
						offsetX = DiagramGenerator.OFFSET_X;
						offsetY = 0.0;
					}
					
					endPoint = new Point2D.Double(coordPointer.x + offsetX, coordPointer.y + offsetY);
					l = new Line2D.Double(coordPointer, endPoint);
					
					d.addLine(l);
					coordPointer.setLocation(endPoint);
					pointerMovement[0] += offsetX;
					pointerMovement[1] += offsetY;
				}
			}
		}
	}
	
	/**
	 * Checking how many operations there are inside an OperationGroup
	 * 
	 * @param operationGroups	to retrieve the correct OperationGroup object based on the ID
	 * @param n					counter -> needed as an initialize value in a recursive file (no pointer like in C)
	 * @return					the number of operations inside this OperationGroup
	 */
	public int getDepth (ArrayList<OperationGroup> operationGroups, int n)
	{
		int firstDepth = -1, secondDepth = -1;
		OperationGroup og;
		
		if (this.firstOperand > 0 && this.secondOperand > 0)
		{
			n += 2;
			return n;
		}
		else
		{
			n++;
			if (this.firstOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.firstOperand);
				firstDepth = og.getDepth(operationGroups, n);
			}
			
			if (this.secondOperand < 0)
			{
				og = OperationGroupManager.getOperationGroupById(operationGroups, this.secondOperand);
				secondDepth = og.getDepth(operationGroups, n);
			}
			
			// As there are two SITE, get the most number of operations
			return (firstDepth > secondDepth)? firstDepth : secondDepth;
		}
	}
	
	public int getFirstOperand ()
	{
		return this.firstOperand;
	}
	
	public int getSecondOperand ()
	{
		return this.secondOperand;
	}
	
	public int getId ()
	{
		return this.id;
	}
	
	public String getOperator ()
	{
		return this.operator;
	}
}
