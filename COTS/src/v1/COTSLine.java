package v1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class COTSLine extends Line2D.Double {
	private ArrayList<Integer> context = null;
	private boolean dashed = false;
	
	public COTSLine(Point2D.Double startPoint, Point2D.Double endPoint) 
	{
		super(startPoint, endPoint);
	}

	public void setDashed (boolean b)
	{
		this.dashed = b;
	}
	
	public boolean getDashed () 
	{
		return this.dashed;
	}
	
	public void addContext (int c) 
	{
		this.context.add(c);
	}
	
	public ArrayList<Integer> getContext () 
	{
		return this.context;
	}
 }
