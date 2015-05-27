package v1;

import java.awt.geom.Point2D;

public class DiagramGenerator 
{
	// OFFSET X and Y will be used to the determined how long the line should be
	public static final double POINTER_OFFSET_X = 150;
	public static final double POINTER_OFFSET_Y = -150;
	
	// coordPointer is the point where the line should start and the length is determined by the OFFSET constants
	private Point2D.Double coordPointer;
	
	// Stores all Line2D object constructed inside constructLines. By calling initDraw() method, it will generate all lines
	private DrawLine lineDrawer;
	
	public DiagramGenerator ()
	{
		this.coordPointer = new Point2D.Double(0.0, DrawLine.DIAGRAM_CANVAS_HEIGHT);
		this.lineDrawer = new DrawLine();
	}
	
	public void constructLines ()
	{
		// Add the Lines2D.Double object directly to the DrawLine object
		OperationGroup og = OperationGroupManager.getBiggestOperationGroup();
		og.setPrevConcurrency(false);
		og.generateDiagram(this.lineDrawer, this.coordPointer, 1, null, false);
	}
	
	public void generateDiagram ()
	{
		this.lineDrawer.initDraw();
	}
}